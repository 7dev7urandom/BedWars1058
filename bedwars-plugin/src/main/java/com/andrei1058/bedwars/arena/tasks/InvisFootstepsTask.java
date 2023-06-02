package com.andrei1058.bedwars.arena.tasks;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.vipfeatures.api.IVipFeatures;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class InvisFootstepsTask implements Runnable {
    Player player;
    BukkitTask task;
    int ticksSinceParticle = 0;
    public InvisFootstepsTask(Player player) {
        this.player = player;
        task = Bukkit.getScheduler().runTaskTimer(BedWars.plugin, this, 0, 1);
    }
    @Override
    public void run() {
        if(ticksSinceParticle++ < 7) return;
        ticksSinceParticle = 0;
        if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            if(player.getLocation().subtract(0.0, 0.1, 0.0).getBlock().getType() != Material.AIR) {
                player.getWorld().playEffect(player.getLocation().add((Math.random() - 0.5)*0.4, 0.01, (Math.random() - 0.5)*0.4), Effect.FOOTSTEP, 1);
            }
        } else {
            task.cancel();
        }
    }
}
