package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import com.rmb938.bungee.permissions.entity.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;

public class SubCommandGroupGet extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupGet(MN2BungeePermissions plugin) {
        super(plugin, "group get");
        this.setUsage("group <group> get <permission>");
        this.setDescription("View a permission associated with a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (strings.length != 4) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group <group> get <permission>"));
            return;
        }
        String groupName = strings[1];
        String permissionName = strings[3];
        Group group = Group.getGroups().get(groupName);
        if (permissionName.startsWith("-")) {
            permissionName = permissionName.substring(1, permissionName.length());
        }

        ArrayList<Permission> permissions = new ArrayList<>();
        for (Permission permission : group.getPermissions()) {
            String permString = permission.getPermission();
            if (permString.startsWith("-")) {
                permString = permString.substring(1, permString.length());
            }
            if (permString.equalsIgnoreCase(permissionName)) {
                permissions.add(permission);
            }
        }
        if (permissions.isEmpty()) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Group "+group.getGroupName()+" does not have the permission "+permissionName));
            return;
        }
        sender.sendMessage(new TextComponent(ChatColor.YELLOW+"Checking Group "+group.getGroupName()+" for permission "+permissionName));
        for (Permission permission : permissions) {
            String permString;
            boolean perm = true;
            if (permission.getPermission().startsWith("-")) {
                perm = false;
                permString = ChatColor.RED+permission.getPermission().substring(1, permission.getPermission().length());
            } else {
                permString = ChatColor.GREEN+permission.getPermission();
            }
            sender.sendMessage(new TextComponent(permString + ":" + perm + " Server Type: " + permission.getServerType()));
        }
    }
}
