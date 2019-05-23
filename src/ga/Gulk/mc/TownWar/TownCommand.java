package ga.Gulk.mc.TownWar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class TownCommand implements CommandExecutor {
    HashMap<UUID, Boolean> confirmDeleteTown;

    String red;

    public TownCommand() {
        confirmDeleteTown = new HashMap<>();

        red = ChatColor.RED.toString(); //whenever a string is sent to the player as {red + x}, the string becomes red
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Currently running TownWar v" + Static.version);
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player)sender;

            String subcommand = args[0];

            switch (subcommand.toLowerCase()) {
                default:
                    return false;

                case "list":
                    return list(player, args);

                case "players":
                    return listPlayers(player, args);

                case "create":
                    return create(player, args, false);

                case "forcecreate":
                    if (player.hasPermission("townwar.forcecreate")) {
                        return create(player, args, true);
                    } else {
                        return Static.noPerms(player);
                    }

                case "delete":
                case "disband":
                    return delete(player, args, false);

                case "forcedelete":
                case "forcedisband":
                    if (player.hasPermission("townwar.forcedelete")) {
                        return delete(player, args, true);
                    } else {
                        return Static.noPerms(player);
                    }

                case "invite":
                    return invite(player, args);

                case "accept":
                    return accept(player, args);
            }
        }

        return false;
    }

    //separate function for each command (organisation?)

    boolean list(Player player, String[] args) {
        int townLength = Static.towns.size();
        if (townLength < 1) {
            player.sendMessage(red + "No towns found");
            return true;
        }

        int pages = (int)Math.ceil(townLength/5.0);
        int page;

        String toSend = "Towns list page ";

        if (args.length < 2 || args[1].equals("1")) {
            toSend += "1 of " + pages + ":\n";
            page = 1;
        } else {
            try {
                page = Integer.parseInt(args[1]);
                toSend += page + " of " + pages + ":\n";
            } catch (NumberFormatException ex) {
                toSend += "1 of " + pages + ":\n";
                page = 1;
            }
        }

        if (page > pages || page < 1) {
            player.sendMessage(red + "Page number not in range!");
        } else {
            for (int i = (page-1) * 5; i < page*5 && i < townLength; i++) {
                toSend += "    " + (i+1) + ". " + Static.towns.get(i).getName() + "\n";
            }

            player.sendMessage(toSend);
        }
        return true;
    }

    boolean listPlayers(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(red + "Usage: /town players {town} {page}");
            return true;
        }

        Town town = Static.getTown(args[1]);

        if (town == null) {
            player.sendMessage(red + "That town doesn't exist!");
            return true;
        }

        UUID[] players = town.getMembers();
        int length = players.length;
        int pages = (int)Math.ceil(length/5.0);
        int page = 1;

        if (args.length > 2) {
            try {
                page = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {}
        }

        if (page > pages || page < 1) {
            player.sendMessage(red + "Page number not in range!");
            page = 1;
        }

        String toSend = args[1] + "'s members page " + page + " of " + pages + ":\n";
        for (int i = (page-1) * 5; i < page*5 && i < length; i++) {
            toSend += Bukkit.getOfflinePlayer(players[i]).getName() + "\n";
        }

        player.sendMessage(toSend);
        return true;
    }

    boolean create(Player player, String[] args, boolean override) {
        if (args.length < 2) {
            player.sendMessage("Usage: /town create {name}");
            return true;
        }

        String name = args[1];

        if ((name.equalsIgnoreCase("admin") || name.equalsIgnoreCase("public")) && !override) {
            player.sendMessage(red + "You can't use that name!");
            return true;
        }

        for (Town i : Static.towns) {
            if (i.name.equals(name)) {
                player.sendMessage(red + "Town name already used!");
                return true;
            } else if (i.getOwner().equals(player.getUniqueId()) && !override) {
                player.sendMessage(red + "You already own a town!");
                return true;
            }
        }

        Static.towns.add(new Town(player, name));
        player.sendMessage("Created new town " + name);
        return true;
    }

    boolean delete(Player player, String[] args, boolean override) {
        if (args.length < 2) {
            player.sendMessage(red + "Usage: /town create {name}");
            return true;
        }

        String name = args[1];

        Town disband = Static.getTown(name);

        if (args.length > 2) {
            if (args[2].equals("confirm")) {
                try {
                    if (confirmDeleteTown.get(player.getUniqueId())) {
                        Static.towns.remove(disband);
                        disband.deleteTown();

                        player.sendMessage("Disbanded town " + name);
                        confirmDeleteTown.remove(player.getUniqueId());
                        return true;
                    }
                } catch (NullPointerException ex) {} //this player doesn't have any pending deletions
            }
        }

        if (disband == null) {
            player.sendMessage(red + "No town with that name exists.");
        } else if (disband.getOwner().equals(player.getUniqueId()) || override) {
            player.sendMessage("Do you really want to do that? Type /town disband " + name + " confirm" +
                    " to confirm");
            confirmDeleteTown.remove(player.getUniqueId());
            confirmDeleteTown.put(player.getUniqueId(), true);
        } else {
            player.sendMessage(red + "You do not own that town!");
        }
        return true;
    }

    boolean invite(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(red + "Usage: /town invite {town} {player}");
            return true;
        }
        Town tToInvite = Static.getTown(args[1]);
        if (tToInvite == null) {
            player.sendMessage(red + "Could not find that town!");
            return true;
        } else if (!tToInvite.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(red + "You are not the owner of that town!");
            return true;
        }

        Player pToInvite = Bukkit.getPlayer(args[2]);
        if(pToInvite == null) {
            player.sendMessage(red + "That player is not online");
            return true;
        }

        for (UUID i : tToInvite.getMembers()) {
            if (i.equals(pToInvite.getUniqueId())) {
                player.sendMessage(red + "That player is already in that town!");
                return true;
            }
        }

        ArrayList<Integer> inviteeTowns = Static.playerInvites.get(pToInvite.getUniqueId());
        if (inviteeTowns == null) {
            inviteeTowns = new ArrayList<>();
            Static.playerInvites.put(pToInvite.getUniqueId(), inviteeTowns);
        }

        for (Integer i : inviteeTowns) {
            if (Static.getTown(i).equals(tToInvite)) {
                player.sendMessage(red + "That player is already invited that town!");
                return true;
            }
        }

        inviteeTowns.add(tToInvite.getID());
        pToInvite.sendMessage("You have been invited to town " + args[1] + "!\nType /town accept " + args[1] +
                " to accept!");

        player.sendMessage("Invited " + pToInvite.getDisplayName() + " to the town " + args[1] + ".");
        return true;
    }

    boolean accept(Player player, String[] args) {
        Town apply = Static.getTown(args[1]);

        if (apply == null) {
            player.sendMessage(red + "There is no town with that name");
            return true;
        }

        ArrayList<Integer> invitedTowns = Static.playerInvites.get(player.getUniqueId());

        if (invitedTowns != null) {
            for (Integer i : invitedTowns) {
                Town workingTown = Static.getTown(i);
                if (apply == workingTown) {
                    workingTown.addMember(player.getUniqueId());

                    player.sendMessage("You have joined the " + args[1] + " town!");
                    return true;
                }
            }
        }

        player.sendMessage(red + "You aren't invited to that town.");
        return true;
    }
}
