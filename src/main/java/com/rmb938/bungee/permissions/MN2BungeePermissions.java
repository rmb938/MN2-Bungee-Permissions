package com.rmb938.bungee.permissions;

import com.rmb938.bungee.permissions.command.CommandPermissions;
import com.rmb938.bungee.permissions.config.MainConfig;
import com.rmb938.bungee.permissions.database.PermissionsLoader;
import com.rmb938.bungee.permissions.jedis.NetCommandHandlerBTB;
import com.rmb938.bungee.permissions.listners.PlayerListener;
import com.rmb938.bungee.permissions.utils.help.PermissionsHelpMap;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;

public class MN2BungeePermissions extends Plugin{

    private MainConfig mainConfig;
    private PermissionsLoader permissionsLoader;
    private PermissionsHelpMap helpMap;

    public void onEnable() {
        mainConfig = new MainConfig(this);
        try {
            mainConfig.init();
            mainConfig.save();
        } catch (net.cubespace.Yamler.Config.InvalidConfigurationException e) {
            getLogger().log(Level.SEVERE, null, e);
            return;
        }

        helpMap = new PermissionsHelpMap(this);

        CommandPermissions commandPermissions = new CommandPermissions(this);
        commandPermissions.registerCommand();
        permissionsLoader = new PermissionsLoader(this);

        new PlayerListener(this);

        new NetCommandHandlerBTB(this);
    }

    public void onDisable() {

    }

    public PermissionsHelpMap getHelpMap() {
        return helpMap;
    }

    public String uuidFormater(String input) {
        if(input == null || input.equals("")) {
            return null;
        }
        input = input.replace("-", "");
        return input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
    }

    public PermissionsLoader getPermissionsLoader() {
        return permissionsLoader;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }
}
