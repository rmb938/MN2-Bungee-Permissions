package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.base.utils.ChatPaginator;
import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class SubCommandGroupParents extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupParents(MN2BungeePermissions plugin) {
        super(plugin, "group parent");
        this.setUsage("group <group> parents");
        this.setDescription("Lists the parents of a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String groupName = strings[1];
        Group group = Group.getGroups().get(groupName);
        StringBuilder sb = new StringBuilder();
        for (Group g1 : group.getInheritance()) {
            sb.append(g1.getGroupName());
            sb.append("\n");
        }
        int pageNumber = 1;
        if (strings.length >= 4) {
            try {
                pageNumber = Integer.parseInt(strings[3]);
            } catch (Exception ex) {
                sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group <group> parents [page]"));
                return;
            }
        }
        ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(sb.toString(), pageNumber);
        StringBuilder header = new StringBuilder();
        header.append(ChatColor.YELLOW);
        header.append("--------- ");
        header.append(ChatColor.WHITE);
        header.append("Group Parents:");
        header.append(group.getGroupName());
        header.append(" ");
        if (chatPage.getTotalPages() > 1) {
            header.append("(");
            header.append(chatPage.getPageNumber());
            header.append("/");
            header.append(chatPage.getTotalPages());
            header.append(") ");
        }
        header.append(ChatColor.YELLOW);
        for (int i = header.length(); i < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH; i++) {
            header.append("-");
        }
        sender.sendMessage(new TextComponent(header.toString()));
        for (String line : chatPage.getLines()) {
            sender.sendMessage(new TextComponent(line));
        }
    }
}
