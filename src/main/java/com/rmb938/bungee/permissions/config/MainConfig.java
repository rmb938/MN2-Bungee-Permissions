package com.rmb938.bungee.permissions.config;

import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Config;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

public class MainConfig extends Config {

    public MainConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"MN2 Bukkit Permissions Configuration File"};
        CONFIG_FILE = new File(plugin.getDataFolder(), "config.yml");
    }

    @Comment("The name of the default group players are assigned to if they don't already have a group")
    public String defaultGroup = "default";

}
