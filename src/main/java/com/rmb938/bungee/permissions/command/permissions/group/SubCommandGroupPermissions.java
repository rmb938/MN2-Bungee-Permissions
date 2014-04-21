package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.base.utils.ChatPaginator;
import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import com.rmb938.bungee.permissions.entity.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class SubCommandGroupPermissions extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupPermissions(MN2BungeePermissions plugin) {
        super(plugin, "group permissions");
        this.setUsage("group <group> permissions");
        this.setDescription("Lists the permissions the group has set");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String groupName = strings[1];
        Group group = Group.getGroups().get(groupName);
        StringBuilder sb = new StringBuilder();
        for (Permission permission : group.getPermissions()) {
            String permissionString = permission.getPermission();
            boolean perm = true;
            if (permissionString.startsWith("-")) {
                perm = false;
                permissionString = ChatColor.RED+permissionString.substring(1, permissionString.length());
            } else {
                permissionString = ChatColor.GREEN+permissionString;
            }
            sb.append(permissionString);
            sb.append(":");
            sb.append(perm);
            sb.append(" Server Type: ");
            sb.append(permission.getServerType());
            sb.append("\n");
        }
        int pageNumber = 1;
        if (strings.length >= 4) {
            try {
                pageNumber = Integer.parseInt(strings[3]);
            } catch (Exception ex) {
                sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group <group> permissions [page]"));
                return;
            }
        }
        ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(sb.toString(), pageNumber);
        StringBuilder header = new StringBuilder();
        header.append(ChatColor.YELLOW);
        header.append("--------- ");
        header.append(ChatColor.WHITE);
        header.append("Group Permissions: ");
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
