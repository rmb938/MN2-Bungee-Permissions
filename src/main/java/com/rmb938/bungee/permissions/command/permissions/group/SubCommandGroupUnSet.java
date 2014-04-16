package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import com.rmb938.bungee.permissions.entity.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;

public class SubCommandGroupUnSet extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupUnSet(MN2BungeePermissions plugin) {
        super(plugin, "group unset");
        this.setUsage("group <group> unset <permission> [server]");
        this.setDescription("UnSets a permission for a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String groupName = strings[1];
        Group group = Group.getGroups().get(groupName);
        if (strings.length < 4) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group <group> unset <permission> [server]"));
            return;
        }
        String permissionString = strings[3].toLowerCase();
        if (permissionString.startsWith("-")) {
            permissionString = permissionString.substring(1, permissionString.length());
        }
        String server = null;
        if (strings.length == 5) {
            server = strings[4];
        }
        ArrayList<Permission> toRemove = new ArrayList<>();
        for (Permission permission : group.getPermissions()) {
            String perm1 = permission.getPermission();
            if (perm1.startsWith("-")) {
                perm1 = perm1.substring(1, perm1.length());
            }
            if (perm1.equalsIgnoreCase(permissionString)) {
                if (server != null) {
                    if (permission.getServerType().equalsIgnoreCase(server)) {
                        toRemove.add(permission);
                    }
                } else {
                    toRemove.add(permission);
                }
            }
        }
        if (toRemove.isEmpty()) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Group "+group.getGroupName()+" goes not have the permission "+permissionString+" set"+(server != null ? " on server "+server : "")));
        }
        for (Permission permission : toRemove) {
            sender.sendMessage(new TextComponent(ChatColor.GREEN+"You removed the permission "+permission.getPermission()+" from group "+group.getGroupName()+" on server "+permission.getServerType()));
            plugin.getPermissionsLoader().removeGroupPermission(group, permission);
        }
    }
}
