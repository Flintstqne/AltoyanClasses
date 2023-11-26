package cc.sarcxsm.altoyamc;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.player.PlayerInteractEntityEvent;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorClasses implements Listener {

    private JavaPlugin plugin;
    private int armorCheckTaskId;
    private Map<UUID, World.Environment> playerDimensions;
    private Map<UUID, Boolean> playerParticlesEnabled;

    public ArmorClasses(JavaPlugin plugin) {
        this.plugin = plugin;
        playerDimensions = new HashMap<>();
        playerParticlesEnabled = new HashMap<>();
        startArmorCheckTask();
    }

    private void startArmorCheckTask() {
        armorCheckTaskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                checkArmor(player);
            }
        }, 0L, 100L).getTaskId(); // Run the task every 5 seconds (100 ticks)
    }

    private void stopArmorCheckTask() {
        Bukkit.getScheduler().cancelTask(armorCheckTaskId);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> checkArmor((Player) event.getWhoClicked()), 1L);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> checkArmor(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        World.Environment previousDimension = playerDimensions.get(player.getUniqueId());
        World.Environment currentDimension = player.getWorld().getEnvironment();

        if (previousDimension != null && previousDimension != currentDimension) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> checkArmor(player), 1L);
        }

        playerDimensions.put(player.getUniqueId(), currentDimension);
    }

    private void checkArmor(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        // Miner Set
        if (helmet != null && helmet.getType() == Material.IRON_HELMET
                && chestplate != null && chestplate.getType() == Material.IRON_CHESTPLATE
                && leggings != null && leggings.getType() == Material.IRON_LEGGINGS
                && boots != null && boots.getType() == Material.IRON_BOOTS) {

            World.Environment currentDimension = player.getWorld().getEnvironment();

            if (!currentDimension.equals(World.Environment.NETHER)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 1));
            } else {
                player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        }
    }
}
