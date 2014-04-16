package com.rmb938.bungee.permissions.command.permissions.player;

import com.rmb938.bungee.base.utils.ChatPaginator;
import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Map;

public class SubCommandPlayerGroups extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandPlayerGroups(MN2BungeePermissions plugin) {
        super(plugin, "player groups");
        this.setUsage("player <player> groups");
        this.setDescription("Lists the groups the player is a member of");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String uuid = strings[1];
        Map.Entry<String, ArrayList<Group>> entry = plugin.getPermissionsLoader().userGetGroups(uuid);
        if (entry == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"User "+uuid+" not found in database."));
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Group group : entry.getValue()) {
            sb.append(group.getGroupName());
            sb.append("\n");
        }
        int pageNumber = 1;
        if (strings.length >= 4) {
            try {
                pageNumber = Integer.parseInt(strings[3]);
            } catch (Exception ex) {
                sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions player <player> groups [page]"));
                return;
            }
        }
        ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(sb.toString(), pageNumber);
        StringBuilder header = new StringBuilder();
        header.append(ChatColor.YELLOW);
        header.append("--------- ");
        header.append(ChatColor.WHITE);
        header.append("User Groups: ");
        header.append(entry.getKey());
        header.append(" ");
        if (chatPage.getTotalPages() > 1) {
            header.append("(");
            header.append(chatPage.getPageNumber());
            header.append("/");
            header.append(chatPage.getTotalPages());
            header.append(") ");
        }
        sender.sendMessage(new TextComponent(header.toString()));
        for (String line : chatPage.getLines()) {
            sender.sendMessage(new TextComponent(line));
        }
    }
}
