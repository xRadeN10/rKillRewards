package by.xRadeN.Events;

import by.xRadeN.rKillRewards;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.UUID;

public class MoneyRewardEvent implements Listener {

    private final HashMap<UUID, String> lastdamager = new HashMap<>();
    private String translate(String s) {return ChatColor.translateAlternateColorCodes('&', rKillRewards.getInstance().getConfig().getString(s));}
    private boolean enabled(String s) {return rKillRewards.getInstance().getConfig().getBoolean(s);}
    private boolean prefixEnabled() {return !this.enabled("Settings.prefix-enabled");}

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e) {
        Player player = (Player) e.getEntity().getLastDamageCause().getEntity();
        if ((player.getKiller() == null && !lastdamager.isEmpty()) || player.getKiller() != null){
            if (!lastdamager.containsKey(player.getUniqueId())) return;
            Player killer = Bukkit.getServer().getPlayer(lastdamager.get(player.getUniqueId()));
            String group = rKillRewards.getInstance().perms.getPrimaryGroup(killer);
            if (!enabled("Rewards." + group + ".player-reward-enabled")) return;
            String moneyReward = rKillRewards.getInstance().getConfig().getString("Rewards." + group + ".player-reward");
            if (moneyReward == null) {
                if (enabled("Settings.prefix-enabled")) killer.sendMessage(translate("Messages.prefix") + translate("Messages.reward-not-set"));
                    else killer.sendMessage(translate("Messages.reward-not-set"));
                return;
            }
            Player killed = e.getEntity().getPlayer();
            lastdamager.remove(player.getUniqueId());
            double playerKillReward = rKillRewards.getInstance().getConfig().getDouble("Rewards." + group + ".player-reward");
            rKillRewards.getInstance().econ.depositPlayer(killer, playerKillReward);
            if(enabled("Rewards." + group + ".player-custom-reward-enabled"))
                for (String customReward : rKillRewards.getInstance().getConfig().getStringList("Rewards." + group + ".player-custom-reward")) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), customReward.replace("%player%", killer.getName()));
                }
            if (enabled("Settings.death-message-enabled")) {
                String deathMsg = translate("Messages.death-message").replace("%killer", killer.getName());
                if (prefixEnabled()) killed.sendMessage(deathMsg);
                    else killed.sendMessage(translate("Messages.prefix") + deathMsg);
            }
            if (enabled("Settings.player-kill-message-enabled")) {
                String killMsg = translate("Messages.player-kill-message")
                        .replace("%killed%", killed.getName())
                        .replace("%player_reward%", moneyReward);
                if (prefixEnabled()) killer.sendMessage(killMsg);
                    else killer.sendMessage(translate("Messages.prefix") + killMsg);
            }
            if (enabled("Settings.broadcast-message-enabled")) {
                String broadcastMsg = translate("Messages.broadcast-message")
                        .replace("%killer%", killer.getName())
                        .replace("%killed%", killed.getName());
                if (prefixEnabled()) Bukkit.broadcastMessage(broadcastMsg);
                    else Bukkit.broadcastMessage(translate("Messages.prefix") + broadcastMsg);
            }
        }
    }

    @EventHandler
    public void onMonsterKill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null || e.getEntity() instanceof Player) return;
        for (String mobName : rKillRewards.getInstance().getConfig().getStringList("Settings.monsters")) {
            String monsterKilled = e.getEntity().getName();
            if (monsterKilled.equalsIgnoreCase(mobName)) {
                Player player = e.getEntity().getKiller();
                String group = rKillRewards.getInstance().perms.getPrimaryGroup(player);
                if (!enabled("Rewards." + group + ".monster-reward-enabled")) return;
                String reward = rKillRewards.getInstance().getConfig().getString("Rewards." + group + ".monster-reward");
                if (reward == null) {
                    if (prefixEnabled()) player.sendMessage(translate("Messages.reward-not-set"));
                        else player.sendMessage(translate("Messages.prefix") + translate("Messages.reward-not-set"));
                    return;
                }
                double monsterKillReward = rKillRewards.getInstance().getConfig().getDouble("Rewards." + group + ".monster-reward");
                rKillRewards.getInstance().econ.depositPlayer(player, monsterKillReward);
                if(enabled("Rewards." + group + ".monster-custom-reward-enabled"))
                    for (String customReward : rKillRewards.getInstance().getConfig().getStringList("Rewards." + group + ".monster-custom-reward")) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), customReward.replace("%player%", player.getName()));
                    }
                if (enabled("Settings.monster-kill-message-enabled")) {
                    String killMsg = translate("Messages.monster-kill-message")
                            .replace("%killed%", monsterKilled)
                            .replace("%monster_reward%", reward);
                    if (prefixEnabled()) player.sendMessage(killMsg);
                        else player.sendMessage(translate("Messages.prefix") + killMsg);
                }
                break;
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Monster || e.getDamager() instanceof Animals) return;
        if (e.getDamager() instanceof Projectile) if (((Projectile) e.getDamager()).getShooter() instanceof Monster) return;
        UUID damaged = e.getEntity().getUniqueId();
        Player ent = e.getDamager() instanceof Projectile ? (Player) ((Projectile) e.getDamager()).getShooter() : (Player) e.getDamager();
        if (damaged == ent.getUniqueId()) return;
        lastdamager.put(damaged, ent.getName());
    }
}