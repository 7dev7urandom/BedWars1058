package com.andrei1058.bedwars.arena;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.shop.ShopCache;
import com.andrei1058.bedwars.arena.tasks.ReJoinTask;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReJoin {

    private UUID player;
    private Arena arena;
    private BedWarsTeam bwt;
    private ReJoinTask task = null;
    private ArrayList<ShopCache.CachedItem> permanentsAndNonDowngradables = new ArrayList<>();

    private int kills = 0, finalKills = 0, deaths = 0, finalDeaths = 0, beds = 0;

    private static List<ReJoin> reJoinList = new ArrayList<>();

    /**
     * Make rejoin possible for a player
     */
    public ReJoin(Player player, Arena arena, BedWarsTeam bwt, List<ShopCache.CachedItem> cachedArmor) {
        if (exists(player)) getPlayer(player).destroy();
        this.bwt = bwt;
        if (this.bwt.isBedDestroyed()) return;
        this.player = player.getUniqueId();
        this.arena = arena;
        reJoinList.add(this);
        BedWars.debug("Created ReJoin for " + player.getName() + " " + player.getUniqueId() + " at " + arena.getWorldName());
        storeStatsDiff(arena.getPlayerKills(player, false), arena.getPlayerKills(player, true), arena.getPlayerDeaths(player, false), arena.getPlayerDeaths(player, true), arena.getPlayerBedsDestroyed(player));
        if (bwt.getMembers().isEmpty()) task = new ReJoinTask(arena, bwt);
        this.permanentsAndNonDowngradables.addAll(cachedArmor);
    }

    /**
     * Check if a player has stored data
     */
    public static boolean exists(Player pl) {
        BedWars.debug("ReJoin exists check " + pl.getUniqueId());
        for (ReJoin rj : new ArrayList<>(reJoinList)) {
            BedWars.debug("ReJoin exists check list scroll: " + rj.getPl().toString());
            if (rj.getPl().equals(pl.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a player ReJoin
     */
    public static ReJoin getPlayer(Player player) {
        BedWars.debug("ReJoin getPlayer " + player.getUniqueId());
        for (ReJoin rj : new ArrayList<>(reJoinList)) {
            if (rj.getPl().equals(player.getUniqueId())) {
                return rj;
            }
        }
        return null;
    }

    /**
     * Check if can reJoin
     */
    public boolean canReJoin() {
        BedWars.debug("ReJoin canReJoin  check.");
        if (arena == null) {
            BedWars.debug("ReJoin canReJoin arena is null " + player.toString());
            destroy();
            return false;
        }
        if (arena.getStatus() == GameState.restarting) {
            BedWars.debug("ReJoin canReJoin status is restarting " + player.toString());
            destroy();
            return false;
        }
        if (bwt == null) {
            BedWars.debug("ReJoin canReJoin bwt is null " + player.toString());
            destroy();
            return false;
        }
        if (bwt.isBedDestroyed()) {
            BedWars.debug("ReJoin canReJoin bed is destroyed " + player.toString());
            destroy();
            return false;
        }
        return true;
    }

    /**
     * Make a player re-join the arena
     */
    public boolean reJoin(Player player) {
        return arena.reJoin(this, player);
    }

    /**
     * Destroy data and rejoin possibility
     */
    public void destroy() {
        BedWars.debug("ReJoin destroy for " + player.toString());
        reJoinList.remove(this);
    }

    /**
     * Get Player
     */
    public UUID getPlayer() {
        return player;
    }

    /**
     * Get player team
     */
    public BedWarsTeam getBwt() {
        return bwt;
    }

    /**
     * Get arena
     */
    public Arena getArena() {
        return arena;
    }

    /**
     * Save arena stats cache for player in order to save them correctly to database
     */
    private void storeStatsDiff(int kills, int finalKills, int deaths, int finalDeaths, int beds) {
        this.kills = kills;
        this.finalKills = finalKills;
        this.deaths = deaths;
        this.finalDeaths = finalDeaths;
        this.beds = beds;
    }

    public int getKills() {
        return kills;
    }

    public int getFinalDeaths() {
        return finalDeaths;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getFinalKills() {
        return finalKills;
    }

    public int getBeds() {
        return beds;
    }

    public ReJoinTask getTask() {
        return task;
    }

    public UUID getPl(){
        return player;
    }

    @SuppressWarnings("WeakerAccess")
    public List<ShopCache.CachedItem> getPermanentsAndNonDowngradables() {
        return permanentsAndNonDowngradables;
    }
}