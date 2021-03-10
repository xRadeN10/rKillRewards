package by.xRadeN.Commands;

import by.xRadeN.rKillRewards;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class rKillRewardsCmd implements CommandExecutor {

    String translate(String s) { return ChatColor.translateAlternateColorCodes('&', rKillRewards.getInstance().getConfig().getString(s));}
    boolean prefixEnabled() { return rKillRewards.getInstance().getConfig().getBoolean("Settings.prefix-enabled");}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args) {
        if(sender.hasPermission("rkillrewards.reload") || sender.isOp()) {
            switch (args.length < 1 ? 0 :
                        args.length == 1 ? 1 : 2) {
                case 0:
                case 2: {
                    if(this.prefixEnabled()) sender.sendMessage(this.translate("Messages.prefix") + this.translate("Messages.usage"));
                    else sender.sendMessage(this.translate("Messages.usage"));
                    break;
                }
                case 1: {
                    if(args[0].equalsIgnoreCase("reload")) {
                        rKillRewards.getInstance().reloadConfig();
                        rKillRewards.getInstance().saveDefaultConfig();
                        if(this.prefixEnabled()) sender.sendMessage(this.translate("Messages.prefix") + this.translate("Messages.reload-config-message"));
                        else sender.sendMessage(this.translate("Messages.reload-config-message"));
                    } else if(this.prefixEnabled()) sender.sendMessage(this.translate("Messages.prefix") + this.translate("Messages.usage"));
                    else sender.sendMessage(this.translate("Messages.usage"));
                    break;
                }
            }
        } else if(this.prefixEnabled()) sender.sendMessage(this.translate("Messages.prefix") + this.translate("Messages.no-permission-message"));
        else sender.sendMessage(this.translate("Messages.no-permission-message"));
    return false;
    }
}

