package org.infestedstudios.inv.examples;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import org.infestedstudios.inv.PaginatedInfestedInv;
import org.infestedstudios.inv.manager.InfestedInvManager;

/**
 * ExamplePaginatedInventory demonstrates how to use PaginatedInfestedInv.
 */
public class ExamplePaginatedInventory extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register the inventory manager
        InfestedInvManager.register(this);

        // Create a new paginated inventory
        ItemStack nextPageItem = new ItemStack(Material.ARROW); // Example item for next page
        ItemStack prevPageItem = new ItemStack(Material.FEATHER); // Example item for previous page

        PaginatedInfestedInv paginatedInv = new PaginatedInfestedInv(54, "My Paginated Inventory", nextPageItem, prevPageItem);

        // Add items to pages
        for (int i = 0; i < 100; i++) {
            ItemStack item = new ItemStack(Material.STONE, i + 1);
            paginatedInv.addItemToPage(item, event -> {
                event.getWhoClicked().sendMessage("Clicked on item " + item.getAmount());
            });
        }

        // Open the inventory to a player (example usage)
        Player player = Bukkit.getPlayer("examplePlayer");
        if (player != null) {
            paginatedInv.open(player);
        }
    }
}
