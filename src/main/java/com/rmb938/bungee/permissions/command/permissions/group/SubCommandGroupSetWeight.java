package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class SubCommandGroupSetWeight extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupSetWeight(MN2BungeePermissions plugin) {
        super(plugin, "group setweight");
        this.setUsage("group <group> setweight <weight>");
        this.setDescription("Sets a weight for a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String groupName = strings[1];
        Group group = Group.getGroups().get(groupName);
        if (strings.length < 4) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group <group> setweight <weight>"));
            return;
        }
        int weight = 0;
        try {
            weight = Integer.parseInt(strings[3]);
        } catch (Exception ex) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group <group> setweight <weight>"));
            return;
        }
        plugin.getPermissionsLoader().modifyGroupWeight(group, weight);
        sender.sendMessage(new TextComponent(ChatColor.GREEN+"You set "+group.getGroupName()+"'s weight to "+weight));
    }
}
