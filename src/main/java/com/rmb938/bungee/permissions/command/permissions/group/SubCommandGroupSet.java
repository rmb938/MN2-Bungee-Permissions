package com.rmb938.bungee.permissions.command.permissions.group;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import com.rmb938.bungee.permissions.entity.Group;
import com.rmb938.bungee.permissions.entity.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class SubCommandGroupSet extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandGroupSet(MN2BungeePermissions plugin) {
        super(plugin, "group set");
        this.setUsage("group <group> set <permission> <value> [server]");
        this.setDescription("Sets a permission for a group");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String groupName = strings[1];
        Group group = Group.getGroups().get(groupName);
        if (strings.length < 5) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions group <group> set <permission> <value> [server]"));
            return;
        }
        String permissionString = strings[3].toLowerCase();
        if (permissionString.startsWith("-")) {
            permissionString = permissionString.substring(1, permissionString.length());
        }
        boolean value = true;
        if (strings[4].equalsIgnoreCase("false") || strings[4].equalsIgnoreCase("f")) {
            value = false;
        }
        String server = "global";
        if (strings.length == 6) {
            server = strings[5];
        }
        for (Permission permission : group.getPermissions()) {
            String perm1 = permission.getPermission();
            if (perm1.startsWith("-")) {
                perm1 = perm1.substring(1, perm1.length());
            }
            if (perm1.equalsIgnoreCase(permissionString)) {
                if (permission.getServerType().equalsIgnoreCase(server)) {
                    boolean has = true;
                    if (permission.getPermission().startsWith("-")) {
                        has = false;
                    }
                    sender.sendMessage(new TextComponent(ChatColor.RED+"Group "+group.getGroupName()+" already has the permission "+permissionString+" set to "+has+" on server "+server));
                    return;
                }
            }
        }
        Permission permission = new Permission();
        permission.setPermission(value == true ? permissionString : "-"+permissionString);
        permission.setServerType(server);
        plugin.getPermissionsLoader().addGroupPermission(group, permission);
        sender.sendMessage(new TextComponent(ChatColor.GREEN+"You set the permission " + permissionString + " to "+value+" on server "+server+" for group "+group.getGroupName()));
    }
}
