package com.github.idimabr;

import com.github.idimabr.listeners.BreakListener;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class RaphaAutoPickup extends JavaPlugin {

    private static WorldGuardPlugin WG;

    @Override
    public void onEnable() {
        // Plugin startup logic

        WG = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if(WG == null){
            System.out.println("Não foi encontrado WorldGuard");
            getPluginLoader().disablePlugin(this);
        }

        Bukkit.getPluginManager().registerEvents(new BreakListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static WorldGuardPlugin getWorldGuard() {
        return WG;
    }
}
