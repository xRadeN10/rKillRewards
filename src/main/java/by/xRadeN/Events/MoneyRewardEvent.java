package by.xRadeN.Events;

import by.xRadeN.rKillRewards;
import me.clip.placeholderapi.PlaceholderAPI;
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
    public static String killedName = "";
    public static String killerName = "";
    String translate(String s) {return ChatColor.translateAlternateColorCodes('&', rKillRewards.getInstance().getConfig().getString(s));}
    String regPlaceholder(Player p, String s) {return PlaceholderAPI.setPlaceholders(p, s);}
    boolean enabled(String s) {return rKillRewards.getInstance().getConfig().getBoolean(s);}

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e) {
        Player player = (Player) e.getEntity().getLastDamageCause().getEntity();
        if((player.getKiller() == null && !lastdamager.isEmpty()) || player.getKiller() != null){
            if(!this.lastdamager.containsKey(player.getUniqueId())) return;
            Player killer;
            killer = Bukkit.getServer().getPlayer(this.lastdamager.get(player.getUniqueId()));
            killerName = killer.getName();
            String group = rKillRewards.getInstance().perms.getPrimaryGroup(killer);
            if(!this.enabled("Rewards." + group + ".player-reward-enabled")) return;
            String reward = rKillRewards.getInstance().getConfig().getString("Rewards." + group + ".player");
            if(reward == null) {
                if(this.enabled("Settings.prefix-enabled")) killer.sendMessage(this.translate("Messages.prefix") + this.translate("Messages.reward-not-set"));
                else killer.sendMessage(this.translate("Messages.reward-not-set"));
                return;
            }
            Player killed = e.getEntity().getPlayer();
            killedName = killed.getName();
            this.lastdamager.remove(player.getUniqueId());
            double playerKillReward = rKillRewards.getInstance().getConfig().getDouble("Rewards." + group + ".player");
            rKillRewards.getInstance().econ.depositPlayer(killer, playerKillReward);

            if (this.enabled("Settings.death-message-enabled")) {
                String deathMessage = regPlaceholder(e.getEntity().getPlayer(), this.translate("Messages.death-message"));
                if (this.enabled("Settings.prefix-enabled")) killed.sendMessage(this.translate("Messages.prefix") + deathMessage);
                else killed.sendMessage(deathMessage); }

            if (this.enabled("Settings.player-kill-message-enabled")) {
                String killMessage = regPlaceholder(e.getEntity().getPlayer(), this.translate("Messages.player-kill-message").replace("%rkillrewards_player_reward%", reward));
                if (this.enabled("Settings.prefix-enabled")) killer.sendMessage(this.translate("Messages.prefix") + killMessage);
                else killer.sendMessage(killMessage); }

            if (this.enabled("Settings.broadcast-message-enabled")) {
                String broadcastMessage = regPlaceholder(e.getEntity().getPlayer(), this.translate("Messages.broadcast-message"));
                if (this.enabled("Settings.prefix-enabled")) Bukkit.broadcastMessage(this.translate("Messages.prefix") + broadcastMessage);
                else Bukkit.broadcastMessage(broadcastMessage); }
        }
    }

    @EventHandler
    public void onMonsterKill(EntityDeathEvent e) {
        if(e.getEntity().getKiller() == null || e.getEntity() instanceof Player) return;
        for (String mobName : rKillRewards.getInstance().getConfig().getStringList("Settings.monsters")) {
            String monsterKilled = e.getEntity().getName();
            if (monsterKilled.equalsIgnoreCase(mobName)) {
                Player player = e.getEntity().getKiller();
                String group = rKillRewards.getInstance().perms.getPrimaryGroup(player);
                if(!this.enabled("Rewards." + group + ".monster-reward-enabled")) return;
                String reward = rKillRewards.getInstance().getConfig().getString("Rewards." + group + ".monster");
                if(reward == null) {
                    if (this.enabled("Settings.prefix-enabled")) player.sendMessage(this.translate("Messages.prefix") + this.translate("Messages.reward-not-set"));
                    else player.sendMessage(this.translate("Messages.reward-not-set"));
                    return;
                }
                Entity monster = e.getEntity();
                killedName = monster.getName();
                double monsterKillReward = rKillRewards.getInstance().getConfig().getDouble("Rewards." + group + ".monster");
                rKillRewards.getInstance().econ.depositPlayer(player, monsterKillReward);
                if (this.enabled("Settings.monster-kill-message-enabled")) {
                    String killMessage = regPlaceholder(e.getEntity().getKiller(), this.translate("Messages.monster-kill-message").replace("%rkillrewards_monster_reward%", reward));
                    if (this.enabled("Settings.prefix-enabled"))
                        player.sendMessage(this.translate("Messages.prefix") + killMessage);
                    else player.sendMessage(killMessage);
                }
                break;
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Monster) return;
        if(e.getDamager() instanceof  Projectile)
            if(((Projectile) e.getDamager()).getShooter() instanceof Monster) return;
        UUID damaged = e.getEntity().getUniqueId();
        Player ent = e.getDamager() instanceof Projectile ? (Player) ((Projectile) e.getDamager()).getShooter() : (Player) e.getDamager();
        if (damaged == ent.getUniqueId()) return;
        this.lastdamager.put(damaged, ent.getName());
    }
}