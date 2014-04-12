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
                    plugin.getPermissionsLoader().loadGroups();
                    plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
                        @Override
                        public void run() {
                            for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
                                plugin.getPermissionsLoader().loadUserInfo(player);
                            }
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
