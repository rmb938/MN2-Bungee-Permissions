package com.rmb938.bungee.permissions;

import com.rmb938.bungee.permissions.config.MainConfig;
import com.rmb938.bungee.permissions.database.PermissionsLoader;
import com.rmb938.bungee.permissions.jedis.NetCommandHandlerBTB;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;

public class MN2BungeePermissions extends Plugin{

    private MainConfig mainConfig;
    private PermissionsLoader permissionsLoader;

    public void onEnable() {
        mainConfig = new MainConfig(this);
        try {
            mainConfig.init();
            mainConfig.save();
        } catch (net.cubespace.Yamler.Config.InvalidConfigurationException e) {
            getLogger().log(Level.SEVERE, null, e);
            return;
        }
        permissionsLoader = new PermissionsLoader(this);
        new NetCommandHandlerBTB(this);
    }

    public void onDisable() {

    }

    public PermissionsLoader getPermissionsLoader() {
        return permissionsLoader;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }
}
