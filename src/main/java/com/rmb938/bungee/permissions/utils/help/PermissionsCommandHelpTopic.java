package com.rmb938.bungee.permissions.utils.help;

import com.rmb938.bungee.base.command.ExtendedCommand;
import com.rmb938.bungee.base.utils.help.HelpTopic;
import com.rmb938.bungee.permissions.command.permissions.PermissionSubCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public class PermissionsCommandHelpTopic extends HelpTopic {

    private final Command command;

    public PermissionsCommandHelpTopic(ExtendedCommand command, PermissionSubCommand subCommand) {
        this.command = command;

        name = subCommand.getName();

        // The short text is the first line of the description
        int i = subCommand.getDescription().indexOf("\n");
        if (i > 1) {
            shortText = subCommand.getDescription().substring(0, i - 1);
        } else {
            shortText = subCommand.getDescription();
        }

        StringBuffer sb = new StringBuffer();
        sb.append(ChatColor.GOLD);
        sb.append("Description: ");
        sb.append(ChatColor.WHITE);
        sb.append(subCommand.getDescription());

        sb.append("\n");
        sb.append(ChatColor.GOLD);
        sb.append("Usage: ");
        sb.append(ChatColor.WHITE);
        sb.append(command.getUsage().replace("<command>", command.getName()) + " " + subCommand.getUsage());

        if (command.getAliases().length > 0) {
            sb.append("\n");
            sb.append(ChatColor.GOLD);
            sb.append("Aliases: ");
            sb.append(ChatColor.WHITE);
            sb.append(ChatColor.WHITE);
            sb.append(StringUtils.join(Arrays.asList(command.getAliases()), ", "));
        }
        fullText = sb.toString();
    }

    @Override
    public boolean canSee(CommandSender sender) {
        if (amendedPermission != null) {
            return sender.hasPermission(amendedPermission);
        } else {
            return sender.hasPermission(command.getPermission());
        }
    }
}
