package com.rmb938.bungee.permissions.listners;

import com.rmb938.bungee.base.event.GetStoredEvent;
import com.rmb938.bungee.permissions.MN2BungeePermissions;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {

    private final MN2BungeePermissions plugin;

    public PlayerListener(MN2BungeePermissions plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onGetStored(GetStoredEvent event) {
        plugin.getPermissionsLoader().loadUserInfo(event.getPlayer());
    }

}
