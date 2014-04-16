package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class SubCommandGroupAddParent extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupAddParent(MN2BungeePermissions plugin) {
        super(plugin, "group addparent");
        this.setUsage("group <group> addparent <parent>");
        this.setDescription("Adds a parent group to a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (strings.length != 4) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group <group> addparent <parent>"));
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
        if (parent.getInheritance().contains(group)) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Parent group "+parentName+" has "+group.getGroupName()+" as a parent. You cannot loop parents."));
            return;
        }
        if (group.getInheritance().contains(parent)) {
            sender.sendMessage(new TextComponent(ChatColor.RED+parent.getGroupName()+" is already a parent of "+group.getGroupName()));
            return;
        }
        plugin.getPermissionsLoader().addGroupParent(group, parent);
        sender.sendMessage(new TextComponent(ChatColor.GREEN+"You added "+parent.getGroupName()+" as a parent group of "+group.getGroupName()));
    }
}
