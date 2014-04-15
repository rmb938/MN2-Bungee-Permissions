package com.rmb938.bungee.permissions.command.permissions;

import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.entity.Permission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Map;

public class SubCommandPlayerGet extends PermissionSubCommand {

    private final MN2BungeePermissions plugin;

    public SubCommandPlayerGet(MN2BungeePermissions plugin) {
        super(plugin, "player get");
        this.setUsage("player <player> get <permission>");
        this.setDescription("View a permission associated with the player");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        String uuid = strings[1];
        if (strings.length != 4) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"Usage: /permissions player <player> get <permission>"));
            return;
        }
        String permissionString = strings[3];
        Map.Entry<String, ArrayList<Permission>> entry = plugin.getPermissionsLoader().userGetPermissions(uuid);
        if (entry == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"User "+uuid+" not found in database."));
            return;
        }
        ArrayList<Permission> permissions = new ArrayList<>();
        for (Permission permission : entry.getValue()) {
            if (permission.getPermission().equalsIgnoreCase(permissionString)) {
                permissions.add(permission);
            }
        }
        if (permissions.isEmpty()) {
            sender.sendMessage(new TextComponent(ChatColor.RED+"User "+entry.getKey()+" does not have the permission "+permissionString));
            return;
        }
        sender.sendMessage(new TextComponent(ChatColor.YELLOW+"Checking User "+entry.getKey()+" for permission "+permissionString));
        for (Permission permission : permissions) {
            boolean perm = true;
            if (permissionString.startsWith("-")) {
                perm = false;
                permissionString = ChatColor.RED+permissionString.substring(1, permissionString.length());
            } else {
                permissionString = ChatColor.GREEN+permissionString;
            }
            sender.sendMessage(new TextComponent(permissionString+":"+perm+" Server Type: "+permission.getServerType()));
        }
    }
}
