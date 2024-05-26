package org.infestedstudios.inv;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.infestedstudios.inv.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * PaginatedInfestedInv provides an implementation of InfestedInv with pagination support.
 */
public class PaginatedInfestedInv extends InfestedInv {
    private final List<Inventory> pages = new ArrayList<>();
    private int currentPage = 0;
    private final ItemStack nextPageItem;
    private final ItemStack prevPageItem;

    /**
     * Constructor for creating a paginated inventory.
     *
     * @param size         The size of each inventory page.
     * @param title        The title of each inventory page.
     * @param nextPageItem The item used for navigating to the next page.
     * @param prevPageItem The item used for navigating to the previous page.
     */
    public PaginatedInfestedInv(int size, String title, ItemStack nextPageItem, ItemStack prevPageItem) {
        super(size, title);
        this.nextPageItem = nextPageItem;
        this.prevPageItem = prevPageItem;
        addPage();
    }

    /**
     * Adds a new empty page to the paginated inventory.
     */
    private void addPage() {
        pages.add(Bukkit.createInventory(null, getInventory().getSize(), getInventory().getType().name()));
    }

    /**
     * Opens a specific page for the player.
     *
     * @param player The player to open the page for.
     * @param page   The page number to open.
     */
    public void openPage(Player player, int page) {
        if (page < 0 || page >= pages.size()) {
            return;
        }
        this.currentPage = page;
        player.openInventory(pages.get(page));
    }

    /**
     * Adds an item to the current page.
     *
     * @param item    The item to add.
     * @param handler The click handler for the item.
     */
    public void addItemToPage(ItemStack item, Consumer<InventoryClickEvent> handler) {
        Inventory currentInventory = pages.get(currentPage);
        int slot = currentInventory.firstEmpty();
        if (slot >= 0) {
            currentInventory.setItem(slot, item);
            setItemHandler(slot + (currentPage * getInventory().getSize()), handler);
        } else {
            addPage();
            currentInventory = pages.get(pages.size() - 1);
            slot = currentInventory.firstEmpty();
            currentInventory.setItem(slot, item);
            setItemHandler(slot + ((pages.size() - 1) * getInventory().getSize()), handler);
        }
    }

    /**
     * Adds an item to the current page using ItemBuilder.
     *
     * @param builder The ItemBuilder to build the item.
     */
    public void addItemToPage(ItemBuilder builder) {
        addItemToPage(builder.build(), builder.getClickHandler());
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        super.onClick(event);
        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(nextPageItem)) {
            openPage((Player) event.getWhoClicked(), currentPage + 1);
        } else if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(prevPageItem)) {
            openPage((Player) event.getWhoClicked(), currentPage - 1);
        }
    }
}
