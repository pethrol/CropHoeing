package me.procq.spigotplugins.crophoeing;

import me.procq.spigotplugins.crophoeing.Listeners.CropBreak;
import org.bukkit.plugin.java.JavaPlugin;

public final class CropHoeing extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new CropBreak(this),this);


    }

}
