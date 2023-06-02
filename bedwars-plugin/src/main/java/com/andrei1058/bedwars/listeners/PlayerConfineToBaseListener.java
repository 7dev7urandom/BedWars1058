package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.NextEvent;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerConfineToBaseListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        IArena a = Arena.getArenaByPlayer(e.getPlayer());
        if(a == null || a.getNextEvent() != NextEvent.GAME_START) return;
        if(e.getTo().distance(a.getTeam(e.getPlayer()).getSpawn()) > 15) {
            e.setCancelled(true);
        }
    }
}
