package by.xRadeN.Events;

import by.xRadeN.rKillRewards;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MonsterRewardEvent implements Listener {

    private String translate(String s) {return ChatColor.translateAlternateColorCodes('&', rKillRewards.getInstance().getConfig().getString(s));}
    private boolean enabled(String s) {return rKillRewards.getInstance().getConfig().getBoolean(s);}
    private boolean prefixEnabled() {return !this.enabled("Settings.prefix-enabled");}

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
                if (enabled("Settings.monster-reward-message-enabled")) {
                    String killMsg = translate("Messages.monster-reward-message")
                            .replace("%killed%", monsterKilled)
                            .replace("%monster_reward%", reward);
                    if (prefixEnabled()) player.sendMessage(killMsg);
                    else player.sendMessage(translate("Messages.prefix") + killMsg);
                }
                break;
            }
        }
    }
}
