package com.rmb938.bungee.permissions.command.permissions;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.entity.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Map;

public class SubCommandPlayerUnSet extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandPlayerUnSet(MN2BungeePermissions plugin) {
        super(plugin, "player unset");
        this.setUsage("player <player> unset <permission> [server]");
        this.setDescription("Sets a permission for a player");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String uuid = strings[1];
        if (strings.length < 4) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions player <player> unset <permission> [server]"));
            return;
        }
        String permissionString = strings[3];
        String server = null;
        if (strings.length == 5) {
            server = strings[4];
        }
        Map.Entry<String, ArrayList<Permission>> entry = plugin.getPermissionsLoader().userGetPermissions(uuid);
        if (entry == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"User "+uuid+" not found in database."));
            return;
        }
        ArrayList<Permission> toRemove = new ArrayList<>();
        for (Permission permission : entry.getValue()) {
            if (permission.getPermission().equalsIgnoreCase(permissionString)) {
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
            sender.sendMessage(new TextComponent(ChatColor.RED+"User "+entry.getKey()+" goes not have the permission "+permissionString+" set"+(server != null ? " on server "+server : "")));
        }
        for (Permission permission : toRemove) {
            sender.sendMessage(new TextComponent(ChatColor.GREEN+"You removed the permission "+permissionString+" from user "+entry.getKey()+" on server "+permission.getServerType()));
            plugin.getPermissionsLoader().userRemovePermission(uuid, permission);
        }
    }
}
