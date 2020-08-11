package me.procq.spigotplugins.crophoeing.Listeners;


import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class CropBreak implements Listener {
    Plugin plugin;
    public CropBreak(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        Vector<Material> crops = new Vector<>();
        crops.add(Material.WHEAT);
        crops.add(Material.BEETROOTS);
        crops.add(Material.POTATOES);
        crops.add(Material.CARROTS);

        Material currentCrop = null;

        for(Material crop : crops) {
           if(block.getType().equals(crop))
                currentCrop = crop;
        }

        //If it is not a crop return;
        if(currentCrop == null) return;


        Player player = event.getPlayer();

        //Do not want to drop items in creative
        if(player.getGameMode().equals(GameMode.CREATIVE))
            return;

        ItemStack mainHandItem = player.getInventory().getItemInMainHand();

        //If player does not have hoe in hand return
        if(!mainHandItem.getType().toString().toLowerCase().endsWith("_hoe")) return;

        Vector<Material> seeds = new Vector<>();
        seeds.add(Material.WHEAT_SEEDS);
        seeds.add(Material.BEETROOT_SEEDS);
        seeds.add(Material.CARROT);
        seeds.add(Material.POTATO);

        if(block.getBlockData() instanceof Ageable) {
            Ageable ageableCrop = (Ageable) block.getBlockData();

            if(ageableCrop.getAge() == ageableCrop.getMaximumAge()) {
                ArrayList<ItemStack> drops = (ArrayList<ItemStack>) block.getDrops();

                Material cropType = block.getType();

                //For every drop check if it is one of seeds types
                for(ItemStack drop : drops) {
                    for(Material seed : seeds ) {
                        if (drop.getType().equals(seed) && drop.getAmount() > 0)
                            drop.setAmount(drop.getAmount() - 1);
                    }

                    try {
                        if(drop.getAmount() > 0) {
                            Objects.requireNonNull(plugin.getServer()
                                    .getWorld(player.getWorld().getUID()))
                                    .dropItemNaturally(block.getLocation(), drop);
                        }
                        event.setDropItems(false);
                    }
                    catch (NullPointerException e) {
                        System.out.println("Cannot find world which player is on.");
                        e.printStackTrace();
                    }

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            block.setType(cropType);
                            ((Ageable) block.getBlockData()).setAge(0);
                        }
                    }.runTaskLater(plugin, 1L);

                }


            }
        }


    }
}
