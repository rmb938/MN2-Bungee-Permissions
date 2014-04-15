package com.rmb938.bungee.permissions.jedis;

import com.rmb938.bungee.base.MN2BungeeBase;
import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.jedis.net.NetChannel;
import com.rmb938.jedis.net.NetCommandHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class NetCommandHandlerBTB extends NetCommandHandler {

    private final MN2BungeePermissions plugin;
    private final MN2BungeeBase basePlugin;

    public NetCommandHandlerBTB(MN2BungeePermissions plugin) {
        NetCommandHandler.addHandler(NetChannel.BUNGEE_TO_BUNGEE, this);
        this.plugin = plugin;
        this.basePlugin = (MN2BungeeBase) plugin.getProxy().getPluginManager().getPlugin("MN2BungeeBase");
    }

    @Override
    public void handle(JSONObject jsonObject) {
        try {
            String fromBungee = jsonObject.getString("from");
            String toBungee = jsonObject.getString("to");

            if (fromBungee.equalsIgnoreCase(basePlugin.getIP())) {
                return;
            }

            if (toBungee.equalsIgnoreCase("*") == false) {
                if (toBungee.equalsIgnoreCase(basePlugin.getIP()) == false) {
                    return;
                }
            }

            String command = jsonObject.getString("command");
            HashMap<String, Object> objectHashMap = objectToHashMap(jsonObject.getJSONObject("data"));
            switch (command) {
                case "reloadGroups":
                    plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
                        @Override
                        public void run() {
                            plugin.getPermissionsLoader().loadGroups();
                            for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
                                plugin.getPermissionsLoader().loadUserInfo(player);
                            }
                        }
                    }, 1, TimeUnit.MILLISECONDS);
                    break;
                case "reloadUser":
                    final String playerUUID = (String) objectHashMap.get("playerUUID");
                    plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
                        @Override
                        public void run() {
                            ProxiedPlayer player = null;
                            for (ProxiedPlayer player1 : plugin.getProxy().getPlayers()) {
                                if (player1.getUniqueId().toString().equals(playerUUID)) {
                                    player = player1;
                                    break;
                                }
                            }
                            if (player == null) {
                                return;
                            }
                            plugin.getPermissionsLoader().loadUserInfo(player);
                        }
                    }, 1, TimeUnit.MILLISECONDS);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            plugin.getLogger().log(Level.SEVERE, null, e);
        }
    }
}
