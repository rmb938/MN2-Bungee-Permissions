package com.rmb938.bungee.permissions.command.permissions;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.entity.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Map;

public class SubCommandPlayerSet extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandPlayerSet(MN2BungeePermissions plugin) {
        super(plugin, "player set");
        this.setUsage("player <player> set <permission> <value> [server]");
        this.setDescription("Sets a permission for a player");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String uuid = strings[1];
        if (strings.length < 5) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions player <player> set <permission> <value> [server]"));
            return;
        }
        String permissionString = strings[3];
        boolean value = true;
        if (strings[4].equalsIgnoreCase("false") || strings[4].equalsIgnoreCase("f")) {
            value = false;
        }
        String server = "global";
        if (strings.length == 6) {
            server = strings[5];
        }
        Map.Entry<String, ArrayList<Permission>> entry = plugin.getPermissionsLoader().userGetPermissions(uuid);
        if (entry == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"User "+uuid+" not found in database."));
            return;
        }
        for (Permission permission : entry.getValue()) {
            if (permission.getPermission().equalsIgnoreCase(permissionString)) {
                if (permission.getServerType().equalsIgnoreCase(server)) {
                    boolean has = true;
                    if (permission.getPermission().startsWith("-")) {
                        has = false;
                    }
                    sender.sendMessage(new TextComponent(ChatColor.RED+"User "+entry.getKey()+" already has the permission "+permissionString+" set to "+has+" on server "+server));
                    return;
                }
            }
        }
        if (value == false) {
            permissionString = "-"+permissionString;
        }
        Permission permission = new Permission();
        permission.setPermission(permissionString);
        permission.setServerType(server);
        plugin.getPermissionsLoader().userAddPermission(uuid, permission);
        sender.sendMessage(new TextComponent(ChatColor.GREEN+"You set the permission "
                +(permissionString.startsWith("-") ? permissionString.substring(1, permissionString.length()) : permissionString)+
                " to "+value+" on server "+server+" for user "+entry.getKey()));
    }
}
