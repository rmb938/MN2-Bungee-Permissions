package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class SubCommandGroupRemove extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupRemove(MN2BungeePermissions plugin) {
        super(plugin, "group remove");
        this.setUsage("group <group> remove");
        this.setDescription("removes a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String groupName = strings[1];
        Group group = Group.getGroups().get(groupName);

        if (group.getGroupName().equalsIgnoreCase(plugin.getMainConfig().defaultGroup)) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"You cannot remove the default group."));
            return;
        }
        plugin.getPermissionsLoader().removeGroup(group);
        sender.sendMessage(new TextComponent(ChatColor.GREEN+"You removed group "+group.getGroupName()));
    }
}
