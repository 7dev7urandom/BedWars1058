package com.andrei1058.bedwars;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class InventoryGUI {

    public class InventoryListener implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
//            if(event.getWhoClicked() != player) return;
//            System.out.println("onInventoryClick");
//            System.out.println(event.getClickedInventory());
//            System.out.println(inventory);

//            if(event.getClickedInventory() != inventory) return;
//            System.out.println(event.getClickedInventory() == inventory);
//            if(event.getWhoClicked())
            if(!players.contains(event.getWhoClicked())) return;
            event.setCancelled(true);
//            System.out.println("canceled");
            if(slotHandlers[event.getSlot()] != null) {
                slotHandlers[event.getSlot()].run(event);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            players.remove(event.getPlayer());
        }

        @EventHandler
        public void onInventory(InventoryEvent event) {

        }
    }

    public abstract class ClickHandler {
        public abstract void run(InventoryClickEvent event);
    }

    protected List<Player> players = new ArrayList<>();
    protected Inventory inventory;

    protected ClickHandler[] slotHandlers;
    public InventoryGUI() {
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), BedWars.plugin);
    }

    public abstract Inventory createInventory(Player p);

    public void addItem(ItemStack item, int slot, ClickHandler handler) {
        inventory.setItem(slot, item);
        slotHandlers[slot] = handler;
    }
    public static ItemStack makeItem(Material material, String name) {
        return makeItem(material, name, 1);
    }
    public static ItemStack makeItem(Material material, String name, int count) {
        ItemStack item = new ItemStack(material, count);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    public void addPlayer(Player p) {
        players.add(p);
        if(inventory == null) inventory = createInventory(p);
        else p.openInventory(inventory);
    }
}
