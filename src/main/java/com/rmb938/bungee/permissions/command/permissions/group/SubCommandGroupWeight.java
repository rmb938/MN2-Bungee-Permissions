package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class SubCommandGroupWeight extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupWeight(MN2BungeePermissions plugin) {
        super(plugin, "group weight");
        this.setUsage("group <group> weight");
        this.setDescription("Gets a weight for a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String groupName = strings[1];
        Group group = Group.getGroups().get(groupName);
        sender.sendMessage(new TextComponent(ChatColor.GREEN+"Group "+group.getGroupName()+"'s weight is "+group.getWeight()));
    }
}
