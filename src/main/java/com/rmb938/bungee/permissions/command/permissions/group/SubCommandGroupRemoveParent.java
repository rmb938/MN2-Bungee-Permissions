package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class SubCommandGroupRemoveParent extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupRemoveParent(MN2BungeePermissions plugin) {
        super(plugin, "group removeparent");
        this.setUsage("group <group> removeparent <parent>");
        this.setDescription("Removes a parent group to a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (strings.length != 4) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group <group> removeparent <parent>"));
            return;
        }
        String groupName = strings[1];
        String parentName = strings[3].toLowerCase();
        Group group = Group.getGroups().get(groupName);
        Group parent = Group.getGroups().get(parentName);
        if (parent == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Parent group "+parentName+" does not exist."));
            return;
        }
        if (group.getInheritance().contains(parent) == false) {
            sender.sendMessage(new TextComponent(ChatColor.RED+parent.getGroupName()+" is not a parent of "+group.getGroupName()));
            return;
        }
        plugin.getPermissionsLoader().removeGroupParent(group, parent);
        sender.sendMessage(new TextComponent(ChatColor.GREEN+"You removed "+parent.getGroupName()+" as a parent group of "+group.getGroupName()));
    }
}
