/*
 * BedWars1058 - A bed wars mini-game.
 * Copyright (C) 2021 Andrei Dascălu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: andrew.dascalu@gmail.com
 */

package com.andrei1058.bedwars.arena.team;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.arena.team.ITeamAssigner;
import com.andrei1058.bedwars.api.events.gameplay.TeamAssignEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class TeamAssigner implements ITeamAssigner {

    private void assignPlayer(Player p, IArena arena, ITeam team) {
        TeamAssignEvent e = new TeamAssignEvent(p, team, arena);
        Bukkit.getPluginManager().callEvent(e);
        if (e.isCancelled()) return;
        p.closeInventory();
        if (e.getTeam() == null) return;
        if (e.getTeam().getMembers().size() >= arena.getMaxInTeam()) return;
        e.getTeam().addPlayers(p);
    }
    public void assignTeams(IArena arena) {

        // team up parties first
        LinkedList<List<Player>> parties = new LinkedList<>();

        List<Player> members;
        for (Player player : arena.getPlayers()) {
            members = BedWars.getParty().getMembers(player);
            if (members == null) continue;
            members = new ArrayList<>(members);
            if (members.isEmpty()) continue;
            members.removeIf(member -> !arena.isPlayer(member));
            if (members.isEmpty()) continue;
            parties.add(members);
        }
        // Get the number of players the teams with the fewest players will have
        int numOfTeams = Math.max(Math.max(parties.size(), (int) Math.ceil((double) arena.getPlayers().size() / arena.getMaxInTeam())), BedWars.debug ? 1 : 2);
        int playersPerTeam = arena.getPlayers().size() / numOfTeams;
        int remainingPlayerCount = arena.getPlayers().size() % numOfTeams;

        // Distribute players to teams
        // Player count per team MUST be equal
        // Keep parties together if possible
        LinkedList<List<Player>> teams = new LinkedList<>();
        List<Player> remainingPlayers = new ArrayList<>(arena.getPlayers());
//        if(parties.size() == 1 && parties.get(0).size() == remainingPlayers.size()) {
//            for(int i = 0; i < numOfTeams; i++) {
//                teams.add(new ArrayList());
//            }
//        }
        for (List<Player> party : parties) {
            if(party.size() <= playersPerTeam || (party.size() <= playersPerTeam + 1 && remainingPlayerCount > 0)) {
                teams.add(party);
                remainingPlayers.removeAll(party);
                if(party.size() == playersPerTeam + 1) {
                    remainingPlayerCount--;
                }
            } else {
                // Inform the players that they will be split up
                for (Player player : party) {
                    player.sendMessage("Your party was split for balancing purposes");
                }
            }
        }
        while(teams.size() < numOfTeams) teams.add(new ArrayList());
        // Distribute remaining players to short teams
        for (List<Player> team : teams) {
            while (team.size() < playersPerTeam) {
                team.add(remainingPlayers.remove((int) (Math.random() * remainingPlayers.size())));
            }
        }
        // Distribute extra players
        for (List<Player> team : teams) {
            if(remainingPlayerCount > 0 && team.size() < playersPerTeam + 1) {
                team.add(remainingPlayers.remove((int) (Math.random() * remainingPlayers.size())));
                remainingPlayerCount--;
            } else break;
        }
        // Distribute remaining players
        if(remainingPlayers.size() > 0) {
            teams.add(remainingPlayers);
        }
        // Sanity check
        if(teams.size() != numOfTeams || teams.stream().anyMatch(team -> team.size() != playersPerTeam && team.size() != playersPerTeam + 1)) {
            throw new RuntimeException("Team assignment failed");
        }
        if(teams.stream().mapToInt(List::size).sum() != arena.getPlayers().size()) {
            throw new RuntimeException("Team assignment failed");
        }
        for (List<Player> team : teams) {
            if(team.size() > arena.getMaxInTeam() || team.size() < 1) {
                throw new RuntimeException("Team assignment failed");
            }
        }

        // Assign teams
        double increment = (double) arena.getTeams().size() / teams.size();
        double index = 0;
        for (List<Player> team : teams) {
            for (Player player : team) {
                assignPlayer(player, arena, arena.getTeams().get((int) index));
            }
            index += increment;
        }
    }
}