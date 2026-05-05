package com.blockmart.donutorders.managers;

import com.blockmart.donutorders.DonutOrders;
import com.blockmart.donutorders.models.DonutOrder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DonutManager {

    private final DonutOrders plugin;
    private final DatabaseManager databaseManager;
    private final NamespacedKey donutFlavorKey;
    private final NamespacedKey donutToppingKey;

    public DonutManager(DonutOrders plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.donutFlavorKey = new NamespacedKey(plugin, "donut_flavor");
        this.donutToppingKey = new NamespacedKey(plugin, "donut_topping");
    }

    public void createOrder(UUID playerId, String flavor, String topping, double cost) {
        try {
            databaseManager.insertDonutOrder(new DonutOrder(playerId.toString(), flavor, topping, cost, System.currentTimeMillis()));
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create donut order for player " + playerId, e);
        }
    }

    public boolean canAfford(Player player, double amount) {
        // Placeholder for an actual economy plugin integration (e.g., Vault)
        // For now, assume player always can afford.
        // In a real plugin, you'd use VaultAPI.getEconomy().has(player, amount)
        return true;
    }

    public void deductMoney(Player player, double amount) {
        // Placeholder for an actual economy plugin integration (e.g., Vault)
        // In a real plugin, you'd use VaultAPI.getEconomy().withdrawPlayer(player, amount);
        plugin.getLogger().log(Level.INFO, player.getName() + " spent $" + amount + " on a donut.");
    }

    public void giveDonut(Player player, String flavor, String topping) {
        ItemStack donut = new ItemStack(Material.COOKIE);
        ItemMeta meta = donut.getItemMeta();

        Component donutName = Component.text((topping.equals("none") ? "" : topping + " ") + flavor + " Donut", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD);
        meta.displayName(donutName);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("A delicious ", NamedTextColor.GRAY).append(Component.text(flavor, NamedTextColor.GOLD)).append(Component.text(" donut!", NamedTextColor.GRAY)));
        if (!topping.equals("none")) {
            lore.add(Component.text("Topped with ", NamedTextColor.GRAY).append(Component.text(topping, NamedTextColor.AQUA)).append(Component.text(".", NamedTextColor.GRAY)));
        }
        meta.lore(lore);

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(donutFlavorKey, PersistentDataType.STRING, flavor);
        container.set(donutToppingKey, PersistentDataType.STRING, topping);

        donut.setItemMeta(meta);

        Bukkit.getScheduler().runTask(plugin, () -> {
            player.getInventory().addItem(donut);
        });
    }

    public boolean isDonut(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.COOKIE) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return false;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(donutFlavorKey, PersistentDataType.STRING); // Check for our custom NBT tag
    }
}