package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class SubCommandGroupCreate extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupCreate(MN2BungeePermissions plugin) {
        super(plugin, "group create");
        this.setUsage("group <group> create");
        this.setDescription("Creates a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String groupName = strings[1];
        Group group = Group.getGroups().get(groupName);
        if (group != null) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"The group "+group.getGroupName()+" already exists."));
            return;
        }
        plugin.getPermissionsLoader().createGroup(groupName, 0);
        group = Group.getGroups().get(groupName);
        sender.sendMessage(new TextComponent(ChatColor.GREEN+"You created the group "+group.getGroupName()));
    }
}
