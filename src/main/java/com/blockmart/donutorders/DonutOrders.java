package com.blockmart.donutorders;

import com.blockmart.donutorders.commands.DonutOrderCommand;
import com.blockmart.donutorders.listeners.InventoryClickListener;
import com.blockmart.donutorders.managers.DonutManager;
import com.blockmart.donutorders.managers.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class DonutOrders extends JavaPlugin {

    private DatabaseManager databaseManager;
    private DonutManager donutManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.databaseManager = new DatabaseManager(this);
        this.donutManager = new DonutManager(this, databaseManager);

        databaseManager.loadDatabase();

        Objects.requireNonNull(getCommand("donutorder")).setExecutor(new DonutOrderCommand(this, donutManager));
        getServer().getPluginManager().registerEvents(new InventoryClickListener(donutManager), this);

        getLogger().log(Level.INFO, "DonutOrders has been enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }
        getLogger().log(Level.INFO, "DonutOrders has been disabled!");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public DonutManager getDonutManager() {
        return donutManager;
    }

    public static Component prefix() {
        return Component.text("[DonutOrders] ", NamedTextColor.GOLD);
    }
}