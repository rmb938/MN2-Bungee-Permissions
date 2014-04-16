package com.rmb938.bungee.permissions.command.permissions.player;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Map;

public class SubCommandPlayerRemoveGroup extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandPlayerRemoveGroup(MN2BungeePermissions plugin) {
        super(plugin, "player removegroup");
        this.setUsage("player <player> removegroup <group>");
        this.setDescription("Removes a player from a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String uuid = strings[1];
        if (strings.length != 4) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions player <player> removegroup <group>"));
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
        if (entry.getValue().contains(group) == false) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"User "+entry.getKey()+" is not in group "+group.getGroupName()));
            return;
        }
        plugin.getPermissionsLoader().userRemoveGroup(uuid, group);
        sender.sendMessage(new TextComponent(ChatColor.GREEN+"You remove user "+entry.getKey()+" from group "+group.getGroupName()));
    }
}
