package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.base.utils.ChatPaginator;
import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class SubCommandGroupList extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupList(MN2BungeePermissions plugin) {
        super(plugin, "group list");
        this.setUsage("group list");
        this.setDescription("Lists the groups");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        StringBuilder sb = new StringBuilder();
        for (Group group : Group.getGroups().values()) {
            sb.append(group.getGroupName());
            sb.append("\n");
        }
        int pageNumber = 1;
        if (strings.length >= 3) {
            try {
                pageNumber = Integer.parseInt(strings[3]);
            } catch (Exception ex) {
                sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group list [page]"));
                return;
            }
        }
        ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(sb.toString(), pageNumber);
        StringBuilder header = new StringBuilder();
        header.append(ChatColor.YELLOW);
        header.append("--------- ");
        header.append(ChatColor.WHITE);
        header.append("Groups:");
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
