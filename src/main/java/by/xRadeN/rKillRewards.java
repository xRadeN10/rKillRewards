package by.xRadeN;

import by.xRadeN.Commands.rKillRewardsCmd;
import by.xRadeN.Events.MonsterRewardEvent;
import by.xRadeN.Events.PlayerRewardEvent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.time.StopWatch;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public final class rKillRewards extends JavaPlugin {

    public Economy econ = null;
    public Permission perms = null;
    private static rKillRewards instance;
    public static rKillRewards getInstance() {
        return instance;
    }
    public rKillRewards(){
    }

    @Override
    public void onEnable() {
            StopWatch sw = new StopWatch();
            sw.start();
            Bukkit.getLogger().log(Level.INFO, "[rKillRewards] Loading plugin...");
            this.getConfig().options().copyDefaults();
            this.saveDefaultConfig();
            this.setupEvents();
            this.setupCommands();
            this.setupbStats();
            this.setupEconomy();
            this.setupPermissions();
            Bukkit.getLogger().log(Level.INFO, "[rKillRewards] Loaded in " + sw.getTime() + "ms");
            sw.stop();
            instance = this;
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                this.econ = rsp.getProvider();
                return true;
            }
        }
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private void setupEvents() {
        getServer().getPluginManager().registerEvents(new PlayerRewardEvent(), this);
        getServer().getPluginManager().registerEvents(new MonsterRewardEvent(), this);
    }

    private void setupCommands() { getCommand("rkillrewards").setExecutor(new rKillRewardsCmd()); }

    private void setupbStats() {
        int pluginId = 10683; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
    }
}
