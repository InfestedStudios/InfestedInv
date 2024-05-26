package org.infestedstudios.inv.examples;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import org.infestedstudios.inv.InfestedInv;
import org.infestedstudios.inv.manager.InfestedInvManager;
import org.infestedstudios.inv.templates.InventoryTemplate;

/**
 * ExampleInventoryTemplate demonstrates how to use InventoryTemplate.
 */
public class ExampleInventoryTemplate extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register the inventory manager
        InfestedInvManager.register(this);

        // Create a new inventory
        InfestedInv inv = new InfestedInv(27, "Template Inventory");

        // Create a template
        InventoryTemplate template = new InventoryTemplate();
        template.addTemplateItem(0, new ItemStack(Material.DIAMOND), event -> {
            event.getWhoClicked().sendMessage("Clicked on diamond!");
        });
        template.addTemplateItem(8, new ItemStack(Material.EMERALD), event -> {
            event.getWhoClicked().sendMessage("Clicked on emerald!");
        });

        // Apply the template to the inventory
        template.applyTemplate(inv);

        // Open the inventory to a player (example usage)
        Player player = Bukkit.getPlayer("examplePlayer");
        if (player != null) {
            inv.open(player);
        }
    }
}
