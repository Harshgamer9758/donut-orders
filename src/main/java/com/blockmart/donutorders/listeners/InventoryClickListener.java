package com.blockmart.donutorders.listeners;

import com.blockmart.donutorders.managers.DonutManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private final DonutManager donutManager;

    public InventoryClickListener(DonutManager donutManager) {
        this.donutManager = donutManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();

        if (donutManager.isDonut(clickedItem)) {
            // Prevent players from moving or duplicating NBT-tagged donuts easily.
            // This is a basic form of protection for items with custom NBT.
            event.setCancelled(true);
        }
    }
}