package cc.sarcxsm.altoyamc;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private JavaPlugin plugin;
    private ArmorClasses armorClasses;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ArmorClasses(this), this);
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
