package by.xRadeN;

import by.xRadeN.Commands.rKillRewardsCmd;
import by.xRadeN.Events.MoneyRewardEvent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.time.StopWatch;
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
            this.getConfig().options().copyDefaults();
            this.saveDefaultConfig();
            this.setupEconomy();
            this.setupEvents();
            this.setupPermissions();
            Bukkit.getLogger().log(Level.INFO, "[rKillRewards] Loaded in " + sw.getTime() + "ms");
            sw.stop();
            getCommand("rkillrewards").setExecutor(new rKillRewardsCmd());
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
        getServer().getPluginManager().registerEvents(new MoneyRewardEvent(), this);
    }
}
