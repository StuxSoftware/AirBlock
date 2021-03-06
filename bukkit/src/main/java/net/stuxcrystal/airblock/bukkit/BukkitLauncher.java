package net.stuxcrystal.airblock.bukkit;

import net.stuxcrystal.airblock.BackendEntryPoint;
import net.stuxcrystal.airblock.Bootstrapper;
import net.stuxcrystal.airblock.EntryPoint;
import net.stuxcrystal.airblock.configuration.parser.files.yaml.YamlGenerator;
import net.stuxcrystal.airblock.configuration.storage.storage.ConfigurationStorage;
import net.stuxcrystal.airblock.configuration.storage.storage.multi.MultiStorage;
import net.stuxcrystal.airblock.configuration.storage.storage.simple.LocalStorage;
import net.stuxcrystal.airblock.configuration.storage.storage.simple.ResourceStreamStorage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Launcher for Bukkit-Systems.
 */
public class BukkitLauncher extends JavaPlugin implements BackendEntryPoint, Listener {

    BukkitServerBackend backend = null;

    @Override
    public void onLoad() {
        this.backend = new BukkitServerBackend(this);
        Bootstrapper.begin(this, backend);
    }

    @Override
    public void onEnable() {
        if (this.backend == null) {
            this.onLoad();
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        Bootstrapper.start(this);
    }

    @Override
    public void onDisable() {
        Bootstrapper.end(this);
        this.backend = null;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent pje) {
        Bootstrapper.login(this, pje.getPlayer());
    }

    @EventHandler
    public void onLogoff(PlayerQuitEvent pqe) {
        Bootstrapper.logoff(this, pqe.getPlayer());
    }

    public void init(EntryPoint entryPoint) {

    }

    public void begin(EntryPoint entryPoint) {

    }

    public void reloading(EntryPoint entryPoint) {

    }

    public void reloaded(EntryPoint entryPoint) {

    }

    public void end(EntryPoint entryPoint) {

    }

    public void deinit(EntryPoint entryPoint) {

    }

    public ConfigurationStorage getBaseConfigurationStorage() {
        return new MultiStorage(
                new LocalStorage(this.getDataFolder(), ".yml", new YamlGenerator()),
                new ConfigurationStorage[]{
                        new ResourceStreamStorage(this.getClassLoader(), "/", ".yml", new YamlGenerator())
                }
        );
    }
}
