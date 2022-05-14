package com.github.idimabr;

import com.github.idimabr.listeners.BreakListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RaphaAutoPickup extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new BreakListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
