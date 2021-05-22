package kr.kro.hurdoo.finddiamond;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("diamond").setExecutor(new DiamondFinder());
    }
}