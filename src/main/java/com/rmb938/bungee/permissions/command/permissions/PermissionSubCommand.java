package com.rmb938.bungee.permissions.command.permissions;

import com.rmb938.bungee.base.command.ExtendedCommand;
import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.utils.help.PermissionsCommandHelpTopic;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;

public abstract class PermissionSubCommand extends ExtendedCommand {

    private static HashMap<String, PermissionSubCommand> subCommandMap = new HashMap<>();

    public static HashMap<String, PermissionSubCommand> getSubCommandHashMap() {
        return subCommandMap;
    }

    public static void registerCommand(MN2BungeePermissions plugin, ExtendedCommand command,  PermissionSubCommand subCommand) {
        subCommandMap.put(subCommand.getName(), subCommand);
        plugin.getHelpMap().addTopic(new PermissionsCommandHelpTopic(command, subCommand));
    }

    public PermissionSubCommand(Plugin plugin, String name) {
        super(plugin, name);
    }

    public PermissionSubCommand(Plugin plugin, String name, String permission) {
        super(plugin, name, permission);
    }

    public PermissionSubCommand(Plugin plugin, String name, String permission, String... aliases) {
        super(plugin, name, permission, aliases);
    }

}
