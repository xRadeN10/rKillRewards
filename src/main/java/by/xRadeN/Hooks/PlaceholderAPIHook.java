package by.xRadeN.Hooks;

import by.xRadeN.Events.MoneyRewardEvent;
import by.xRadeN.rKillRewards;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
public class PlaceholderAPIHook extends PlaceholderExpansion {
    private rKillRewards plugin;

    public PlaceholderAPIHook(rKillRewards plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "rkillrewards";
    }

    @Override
    public String getAuthor(){
        return rKillRewards.getInstance().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return null;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier){

        // %rkillrewards_killer%
        if(identifier.equals("killer")){
            return MoneyRewardEvent.killerName;
        }

        // %rkillrewards_killed%
        if(identifier.equals("killed")){
            return MoneyRewardEvent.killedName;
        }

        return null;
    }
}