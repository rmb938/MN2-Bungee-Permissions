package com.rmb938.bungee.permissions.command;

import com.rmb938.bungee.base.command.ExtendedCommand;
import com.rmb938.bungee.base.utils.ChatPaginator;
import com.rmb938.bungee.base.utils.help.HelpMap;
import com.rmb938.bungee.base.utils.help.HelpTopic;
import com.rmb938.bungee.base.utils.help.HelpTopicComparator;
import com.rmb938.bungee.base.utils.help.IndexHelpTopic;
import com.rmb938.bungee.base.utils.mojangAPI.profiles.HttpProfileRepository;
import com.rmb938.bungee.base.utils.mojangAPI.profiles.Profile;
import com.rmb938.bungee.base.utils.mojangAPI.profiles.ProfileCriteria;
import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.*;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandPermissions extends ExtendedCommand {

    private final MN2BungeePermissions plugin;

    public CommandPermissions(MN2BungeePermissions plugin) {
        super(plugin, "permissions", "mn2.bungee.permissions", "perm", "permission");
        this.setUsage("/<command>");
        this.setDescription("Commands for MN2 Permissions");
        this.plugin = plugin;
    }

    public void registerCommand() {
        ExtendedCommand.registerCommand(plugin, new CommandPermissions(plugin));
        PermissionSubCommand.registerCommand(plugin, this, new SubCommandPlayerPermissions(plugin));
        PermissionSubCommand.registerCommand(plugin, this, new SubCommandPlayerGet(plugin));
        PermissionSubCommand.registerCommand(plugin, this, new SubCommandPlayerSet(plugin));
        PermissionSubCommand.registerCommand(plugin, this, new SubCommandPlayerUnSet(plugin));
        PermissionSubCommand.registerCommand(plugin, this, new SubCommandPlayerGroups(plugin));
        PermissionSubCommand.registerCommand(plugin, this, new SubCommandPlayerAddGroup(plugin));
        PermissionSubCommand.registerCommand(plugin, this, new SubCommandPlayerRemoveGroup(plugin));
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            showHelp("index", null);
        } else {
            if (args[0].equalsIgnoreCase("player")) {
                if (args.length == 1) {
                    sender.sendMessage(new TextComponent(ChatColor.RED+"You must include a player name with permission player commands"));
                    sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions player <player> <command...>"));
                    return;
                }
                if (args.length == 2) {
                    sender.sendMessage(new TextComponent(ChatColor.RED+"You must include a command with permission player commands"));
                    sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions player <player> <command...>"));
                    return;
                }
                final String subCommand = args[2];
                final String playerName = args[1];
                plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (plugin.getProxy().getPlayer(playerName) == null) {
                            HttpProfileRepository profileRepository = new HttpProfileRepository();
                            Profile[] profiles = profileRepository.findProfilesByCriteria(new ProfileCriteria(playerName, "minecraft"));
                            if (profiles.length == 0) {
                                sender.sendMessage(new TextComponent(ChatColor.RED+"There is no player with the name of "+playerName));
                                return;
                            } else if (profiles.length > 1) {
                                sender.sendMessage(new TextComponent(ChatColor.RED+"There are multiple players with the player name of "+playerName));
                                return;
                            }
                            args[1] = profiles[0].getId();
                            if (args[1].contains("-")) {
                                sender.sendMessage(new TextComponent(ChatColor.RED+"Mojang fixed uuids. Please update the plugin."));
                                return;
                            }
                            args[1] = args[1].replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
                        } else {
                            args[1] = plugin.getProxy().getPlayer(playerName).getUniqueId().toString();
                        }
                        plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
                            @Override
                            public void run() {
                                if (PermissionSubCommand.getSubCommandHashMap().containsKey("player "+subCommand)) {
                                    PermissionSubCommand.getSubCommandHashMap().get("player "+subCommand).execute(sender, args);
                                } else {
                                    sender.sendMessage(new TextComponent(ChatColor.RED+"Unknown permission player command."));
                                    sender.sendMessage(new TextComponent(ChatColor.RED+"Use /permissions help player to view all permission player commands."));
                                }
                            }
                        }, 1, TimeUnit.MILLISECONDS);
                    }
                });
            } else if (args[0].equalsIgnoreCase("group")) {
                if (args.length == 1) {
                    sender.sendMessage(new TextComponent(ChatColor.RED+"You must include a group name with permission group commands"));
                    sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group <group> <command...>"));
                    return;
                }
                String groupName = args[1].toLowerCase();
                if (groupName.equals("list")) {
                    PermissionSubCommand.getSubCommandHashMap().get("list").execute(sender, args);
                    return;
                }
                if (Group.getGroups().containsKey(groupName) == false) {
                    sender.sendMessage(new TextComponent(ChatColor.RED+"Unknown permissions group "+groupName));
                    return;
                }
                if (args.length == 2) {
                    sender.sendMessage(new TextComponent(ChatColor.RED+"You must include a command with permission group commands"));
                    sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group <group> <command...>"));
                    return;
                }
                String subCommand = args[2];
                if (PermissionSubCommand.getSubCommandHashMap().containsKey("group "+subCommand)) {
                    PermissionSubCommand.getSubCommandHashMap().get("group "+subCommand).execute(sender, args);
                } else {
                    sender.sendMessage(new TextComponent(ChatColor.RED+"Unknown permission group command."));
                    sender.sendMessage(new TextComponent(ChatColor.RED+"Use /permissions help group to view all permission group commands."));
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                String command;
                int pageNumber;
                int pageHeight;
                int pageWidth;
                if (args.length == 1) {
                    command = "";
                    pageNumber = 1;
                } else if (NumberUtils.isDigits(args[args.length -1])) {
                    String[] join = Arrays.copyOfRange(args, 1, args.length - 1);
                    command = StringUtils.join(Arrays.asList(join), " ");
                    try {
                        pageNumber = NumberUtils.createInteger(args[args.length - 1]);
                    } catch (NumberFormatException exception) {
                        pageNumber = 1;
                    }
                    if (pageNumber <= 0) {
                        pageNumber = 1;
                    }
                } else {
                    command = StringUtils.join(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)), " ");
                    pageNumber = 1;
                }

                pageHeight = ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT - 1;
                pageWidth = ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH;

                HelpMap helpMap = plugin.getHelpMap();
                HelpTopic topic = helpMap.getHelpTopic(command);
                plugin.getLogger().info("Command: "+command);

                if (topic == null) {
                    topic = findPossibleMatches(command);
                }

                if (topic == null || !topic.canSee(sender)) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "No help for " + command));
                    return;
                }

                ChatPaginator.ChatPage page = ChatPaginator.paginate(topic.getFullText(sender), pageNumber, pageWidth, pageHeight);

                StringBuilder header = new StringBuilder();
                header.append(ChatColor.YELLOW);
                header.append("--------- ");
                header.append(ChatColor.WHITE);
                header.append("Permissions Help: ");
                header.append(topic.getName());
                header.append(" ");
                if (page.getTotalPages() > 1) {
                    header.append("(");
                    header.append(page.getPageNumber());
                    header.append("/");
                    header.append(page.getTotalPages());
                    header.append(") ");
                }
                header.append(ChatColor.YELLOW);
                for (int i = header.length(); i < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH; i++) {
                    header.append("-");
                }
                sender.sendMessage(new TextComponent(header.toString()));

                for (String line : page.getLines()) {
                    sender.sendMessage(new TextComponent(line));
                }
            }
        }
    }

    public String showHelp(String topic, String command) {
        StringBuilder help = new StringBuilder();

        switch (topic) {
            case "group":
                if (command == null) {
                    help.append("/permissions group <group> create [weight]\n");
                    help.append("/permissions group <group> weight\n");
                    help.append("/permissions group <group> setweight <weight>\n");

                    help.append("/permissions group <group> permissions\n");
                    help.append("/permissions group <group> get <permission>\n");
                    help.append("/permissions group <group> set <permission> [server] [value]\n");
                    help.append("/permissions group <group> unset <permission> [server]\n");

                    help.append("/permissions group <group> parents\n");
                    help.append("/permissions group <group> addparent <parent>\n");
                    help.append("/permissions group <group> removeparent <parent>\n");
                }
                /*help.append("/permissions group <group> create [weight] - creates a group with an optional weight\n");
                help.append("/permissions group <group> weight - shows the weight of a group\n");
                help.append("/permissions group <group> setweight <weight> - modifies the weight of a group\n");

                help.append("/permissions group <group> permissions - lists the permissions the group has set\n");
                help.append("/permissions group <group> get <permission> - view a permission associated with a group\n");
                help.append("/permissions group <group> set <permission> [server] [value] - sets a permission for a group\n");
                help.append("/permissions group <group> unset <permission> [server] - unsets a permission for a group\n");

                help.append("/permissions group <group> parents - shows the parents of the group\n");
                help.append("/permissions group <group> addparent <parent> - adds a parent to the group\n");
                help.append("/permissions group <group> removeparent <parent> - removes a parent from the group\n");*/
                break;
            case "player":
                if (command == null) {
                    help.append("/permissions player <player> permissions\n");
                    help.append("/permissions player <player> get <permission>\n");
                    help.append("/permissions player <player> set <permission> <value> [server]\n");
                    help.append("/permissions player <player> unset <permission> [server]\n");

                    help.append("/permissions player <player> groups\n");
                    help.append("/permissions player <player> addgroup <group> \n");
                    help.append("/permissions player <player> removegroup <group>\n");
                }
                /*help.append("/permissions player <player> permissions - lists the permissions the player has set\n");
                help.append("/permissions player <player> get <permission> - view a permission associated with a player\n");
                help.append("/permissions player <player> set <permission> [server] [value] - sets a permission for a player\n");
                help.append("/permissions player <player> unset <permission> [server] - unsets a permission for a player\n");

                help.append("/permissions player <player> groups - list the groups a player is a member of\n");
                help.append("/permissions player <player> addgroup <group> - add player as a member of a group\n");
                help.append("/permissions player <player> removegroup <group> - remove player from a group\n");*/
                break;
            default:
                help.append(ChatColor.GRAY);
                help.append("Available Help Topics\n");
                help.append(ChatColor.YELLOW);
                help.append("Group: List of group permission commands\n");
                help.append("Player: List of player permission commands\n");
                break;
        }
        return help.toString();
    }

    protected HelpTopic findPossibleMatches(String searchString) {
        int maxDistance = (searchString.length() / 5) + 3;
        Set<HelpTopic> possibleMatches = new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance());

        if (searchString.startsWith("/")) {
            searchString = searchString.substring(1);
        }

        for (HelpTopic topic : plugin.getHelpMap().getHelpTopics()) {
            String trimmedTopic = topic.getName().startsWith("/") ? topic.getName().substring(1) : topic.getName();

            if (trimmedTopic.length() < searchString.length()) {
                continue;
            }

            if (Character.toLowerCase(trimmedTopic.charAt(0)) != Character.toLowerCase(searchString.charAt(0))) {
                continue;
            }

            if (damerauLevenshteinDistance(searchString, trimmedTopic.substring(0, searchString.length())) < maxDistance) {
                possibleMatches.add(topic);
            }
        }

        if (possibleMatches.size() > 0) {
            return new IndexHelpTopic("Search", null, null, possibleMatches, "Search for: " + searchString);
        } else {
            return null;
        }
    }

    /**
     * Computes the Dameraur-Levenshtein Distance between two strings. Adapted
     * from the algorithm at <a href="http://en.wikipedia.org/wiki/Damerau–Levenshtein_distance">Wikipedia: Damerau–Levenshtein distance</a>
     *
     * @param s1 The first string being compared.
     * @param s2 The second string being compared.
     * @return The number of substitutions, deletions, insertions, and
     * transpositions required to get from s1 to s2.
     */
    protected static int damerauLevenshteinDistance(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return 0;
        }
        if (s1 != null && s2 == null) {
            return s1.length();
        }
        if (s1 == null) {
            return s2.length();
        }

        int s1Len = s1.length();
        int s2Len = s2.length();
        int[][] H = new int[s1Len + 2][s2Len + 2];

        int INF = s1Len + s2Len;
        H[0][0] = INF;
        for (int i = 0; i <= s1Len; i++) {
            H[i + 1][1] = i;
            H[i + 1][0] = INF;
        }
        for (int j = 0; j <= s2Len; j++) {
            H[1][j + 1] = j;
            H[0][j + 1] = INF;
        }

        Map<Character, Integer> sd = new HashMap<>();
        for (char Letter : (s1 + s2).toCharArray()) {
            if (!sd.containsKey(Letter)) {
                sd.put(Letter, 0);
            }
        }

        for (int i = 1; i <= s1Len; i++) {
            int DB = 0;
            for (int j = 1; j <= s2Len; j++) {
                int i1 = sd.get(s2.charAt(j - 1));
                int j1 = DB;

                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    H[i + 1][j + 1] = H[i][j];
                    DB = j;
                } else {
                    H[i + 1][j + 1] = Math.min(H[i][j], Math.min(H[i + 1][j], H[i][j + 1])) + 1;
                }

                H[i + 1][j + 1] = Math.min(H[i + 1][j + 1], H[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1));
            }
            sd.put(s1.charAt(i - 1), i);
        }

        return H[s1Len + 1][s2Len + 1];
    }

}
