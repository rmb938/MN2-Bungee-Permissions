package com.rmb938.bungee.permissions.command.permissions;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Map;

public class SubCommandPlayerAddGroup extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandPlayerAddGroup(MN2BungeePermissions plugin) {
        super(plugin, "player addgroup");
        this.setUsage("player <player> addgroup <group>");
        this.setDescription("Adds a player to a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String uuid = strings[1];
        if (strings.length != 4) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions player <player> addgroup <group>"));
            return;
        }
        String groupName = strings[3].toLowerCase();
        Group group = Group.getGroups().get(groupName);
        if (group == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"The group "+groupName+" does not exist."));
            return;
        }
        Map.Entry<String, ArrayList<Group>> entry = plugin.getPermissionsLoader().userGetGroups(uuid);
        if (entry == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"User "+uuid+" not found in database."));
            return;
        }
        if (entry.getValue().contains(group)) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"User "+entry.getKey()+" is already in group "+group.getGroupName()));
            return;
        }
        plugin.getPermissionsLoader().userAddGroup(uuid, group);
        sender.sendMessage(new TextComponent(ChatColor.GREEN+"You added user "+entry.getKey()+" to group "+group.getGroupName()));
    }
}
