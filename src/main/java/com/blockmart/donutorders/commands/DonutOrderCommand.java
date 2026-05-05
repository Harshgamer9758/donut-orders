package com.blockmart.donutorders.commands;

import com.blockmart.donutorders.DonutOrders;
import com.blockmart.donutorders.managers.DonutManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DonutOrderCommand implements CommandExecutor {

    private final DonutOrders plugin;
    private final DonutManager donutManager;

    public DonutOrderCommand(DonutOrders plugin, DonutManager donutManager) {
        this.plugin = plugin;
        this.donutManager = donutManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(DonutOrders.prefix().append(Component.text("Only players can order donuts.", NamedTextColor.RED)));
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            player.sendMessage(DonutOrders.prefix().append(Component.text("Usage: /donutorder <flavor> [topping]", NamedTextColor.YELLOW)));
            return true;
        }

        String flavor = args[0].toLowerCase();
        String topping = args.length == 2 ? args[1].toLowerCase() : "none";

        double cost = plugin.getConfig().getDouble("donut-prices." + flavor, -1.0);
        if (cost == -1.0) {
            player.sendMessage(DonutOrders.prefix().append(Component.text("Invalid donut flavor. Available flavors: ", NamedTextColor.RED)).append(Component.text(String.join(", ", plugin.getConfig().getConfigurationSection("donut-prices").getKeys(false)), NamedTextColor.WHITE)));
            return true;
        }

        // Simulate escrow/economy check
        if (!donutManager.canAfford(player, cost)) {
            player.sendMessage(DonutOrders.prefix().append(Component.text("You cannot afford this donut! It costs $" + cost + ".", NamedTextColor.RED)));
            return true;
        }

        // Schedule database interaction asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            donutManager.createOrder(player.getUniqueId(), flavor, topping, cost);
            plugin.getServer().getScheduler().runTask(plugin, () ->{
                player.sendMessage(DonutOrders.prefix().append(Component.text("You have ordered a " + (topping.equals("none") ? "" : topping + " ") + flavor + " donut for $" + cost + "!", NamedTextColor.GREEN)));
                // Deduct money in a synchronous block if using Vault or similar
                donutManager.deductMoney(player, cost);
                donutManager.giveDonut(player, flavor, topping);
            });
        });

        return true;
    }
}