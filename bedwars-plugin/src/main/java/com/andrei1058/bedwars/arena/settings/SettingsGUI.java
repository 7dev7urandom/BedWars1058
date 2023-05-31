package com.andrei1058.bedwars.arena.settings;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.InventoryGUI;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class SettingsGUI extends InventoryGUI implements Listener {
    public static Map<IArena, SettingsGUI> guis = new HashMap<>();
    IArena arena;
    ItemStack timerItem;
    public SettingsGUI() {
        Bukkit.getPluginManager().registerEvents(this, BedWars.plugin);
    }

    /**
     * Opens the Teleporter GUI to a Player
     */

    @Override
    public Inventory createInventory(Player p) {
        Inventory inv = Bukkit.createInventory(p, 9, "Arena Settings");
        this.arena = Arena.getArenaByPlayer(p);
        this.inventory = inv;
        slotHandlers = new ClickHandler[9];
        if(arena == null) return null;
        timerItem = makeItem(Material.WATCH, arena.getStartingTask() == null ? "Timer is not running" : arena.getStartingTask().isPaused() ? "Resume timer" : "Pause timer");
        addItem(timerItem, 0, new ClickHandler(){
            @Override
            public void run(InventoryClickEvent event) {
                IArena arena = Arena.getArenaByPlayer((Player) event.getWhoClicked());
                if(arena == null) {
                    event.getWhoClicked().closeInventory();
                    return;
                }
                if(arena.getStartingTask() == null) return;
                arena.getStartingTask().setPaused(!arena.getStartingTask().isPaused());
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                meta.setDisplayName(arena.getStartingTask().isPaused() ? "Resume timer" : "Pause timer");
                event.getCurrentItem().setItemMeta(meta);
            }
        });
        addItem(makeItem(Material.ARROW, "Shorten cooldown"), 1, new ClickHandler() {
            @Override
            public void run(InventoryClickEvent event) {
                ((Player) event.getWhoClicked()).performCommand("bw start");
            }
        });
        addItem(makeItem(Material.NAME_TAG, ((Arena)arena).getSettings().isPlayersNicked() ? "Players will be nicked" : "Players will not be nicked"), 7, new ClickHandler() {
            @Override
            public void run(InventoryClickEvent event) {
                ((Arena) arena).getSettings().togglePlayersNicked();
                ItemMeta meta = event.getCurrentItem().getItemMeta();
                meta.setDisplayName(((Arena)arena).getSettings().isPlayersNicked() ? "Players will be nicked" : "Players will not be nicked");
                event.getCurrentItem().setItemMeta(meta);
            }
        });
        return inv;
    }

    public static void addInventoryForPlayer(Player p) {
        if(!guis.containsKey(Arena.getArenaByPlayer(p))) {
            SettingsGUI gui = new SettingsGUI();
            guis.put(Arena.getArenaByPlayer(p), gui);
        }
        guis.get(Arena.getArenaByPlayer(p)).addPlayer(p);
    }

    @EventHandler
    public void onArenaStatusChange(GameStateChangeEvent event) {
        if(event.getArena() == arena) {
            if(event.getNewState() == GameState.playing) {
                players.forEach(p -> p.closeInventory());
            }
            ItemMeta meta = timerItem.getItemMeta();
            meta.setDisplayName(arena.getStartingTask() == null ? "Timer is not running" : arena.getStartingTask().isPaused() ? "Resume timer" : "Pause timer");
        }
    }

    @EventHandler
    public void onTest(PlayerInteractEvent event) {
        if(event.getPlayer().getItemInHand().getType() == Material.CARROT) {
            event.getPlayer().sendMessage(event.getPlayer().getDisplayName());
        }
    }
}
