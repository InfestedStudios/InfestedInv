package org.infestedstudios.inv;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.infestedstudios.inv.utils.ItemBuilder;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Lightweight and easy-to-use inventory API for Bukkit plugins.
 * This class serves as the base for creating custom inventories with additional functionality.
 */
public class InfestedInv {

    private final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();

    private final Inventory inventory;

    private Predicate<Player> closeFilter;
    private int updateTaskId = -1;

    /**
     * Create a new InfestedInv with a custom size.
     *
     * @param size The size of the inventory.
     */
    public InfestedInv(int size) {
        this(Bukkit.createInventory(null, size));
    }

    /**
     * Create a new InfestedInv with a custom size and title.
     *
     * @param size  The size of the inventory.
     * @param title The title (name) of the inventory.
     */
    public InfestedInv(int size, String title) {
        this(Bukkit.createInventory(null, size, title));
    }

    /**
     * Create a new InfestedInv with a custom type.
     *
     * @param type The type of the inventory.
     */
    public InfestedInv(InventoryType type) {
        this(Bukkit.createInventory(null, type));
    }

    /**
     * Create a new InfestedInv with a custom type and title.
     *
     * @param type  The type of the inventory.
     * @param title The title of the inventory.
     */
    public InfestedInv(InventoryType type, String title) {
        this(Bukkit.createInventory(null, type, title));
    }

    /**
     * Create a new InfestedInv with a given inventory.
     *
     * @param inventory The inventory to be used.
     */
    public InfestedInv(Inventory inventory) {
        Objects.requireNonNull(inventory, "inventory");
        this.inventory = inventory;
    }

    /**
     * Handler for when the inventory is opened.
     *
     * @param event The inventory open event.
     */
    protected void onOpen(InventoryOpenEvent event) {
    }

    /**
     * Handler for when an item in the inventory is clicked.
     *
     * @param event The inventory click event.
     */
    protected void onClick(InventoryClickEvent event) {
    }

    /**
     * Handler for when the inventory is closed.
     *
     * @param event The inventory close event.
     */
    protected void onClose(InventoryCloseEvent event) {
    }

    /**
     * Add an item to the inventory in the first empty slot.
     *
     * @param item The item to add.
     */
    public void addItem(ItemStack item) {
        addItem(item, null);
    }

    /**
     * Add an item to the inventory in the first empty slot with a click handler.
     *
     * @param item    The item to add.
     * @param handler The click handler for the item.
     */
    public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        int slot = this.inventory.firstEmpty();
        if (slot >= 0) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Add an item using ItemBuilder to the inventory in the first empty slot.
     *
     * @param builder The ItemBuilder to build the item.
     */
    public void addItem(ItemBuilder builder) {
        addItem(builder.build(), builder.getClickHandler());
    }

    /**
     * Set an item in a specific slot.
     *
     * @param slot The slot to set the item in.
     * @param item The item to set.
     */
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Set an item in a specific slot with a click handler.
     *
     * @param slot    The slot to set the item in.
     * @param item    The item to set.
     * @param handler The click handler for the item.
     */
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.inventory.setItem(slot, item);
        setItemHandler(slot, handler);
    }

    /**
     * Set an item in a specific slot using ItemBuilder.
     *
     * @param slot    The slot to set the item in.
     * @param builder The ItemBuilder to build the item.
     */
    public void setItem(int slot, ItemBuilder builder) {
        setItem(slot, builder.build(), builder.getClickHandler());
    }

    /**
     * Set an item in a range of slots.
     *
     * @param slotFrom The starting slot.
     * @param slotTo   The ending slot.
     * @param item     The item to set.
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item) {
        setItems(slotFrom, slotTo, item, null);
    }

    /**
     * Set an item in a range of slots with a click handler.
     *
     * @param slotFrom The starting slot.
     * @param slotTo   The ending slot.
     * @param item     The item to set.
     * @param handler  The click handler for the item.
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i <= slotTo; i++) {
            setItem(i, item, handler);
        }
    }

    /**
     * Set an item in multiple slots.
     *
     * @param slots The slots to set the item in.
     * @param item  The item to set.
     */
    public void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    /**
     * Set an item in multiple slots with a click handler.
     *
     * @param slots   The slots to set the item in.
     * @param item    The item to set.
     * @param handler The click handler for the item.
     */
    public void setItems(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Remove an item from a specific slot.
     *
     * @param slot The slot to remove the item from.
     */
    public void removeItem(int slot) {
        this.inventory.clear(slot);
        this.itemHandlers.remove(slot);
    }

    /**
     * Remove items from multiple slots.
     *
     * @param slots The slots to remove the items from.
     */
    public void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    /**
     * Set a close filter to prevent players from closing the inventory.
     *
     * @param closeFilter The close filter predicate.
     */
    public void setCloseFilter(Predicate<Player> closeFilter) {
        this.closeFilter = closeFilter;
    }

    /**
     * Add a handler for inventory open events.
     *
     * @param openHandler The open handler to add.
     */
    public void addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        this.openHandlers.add(openHandler);
    }

    /**
     * Add a handler for inventory close events.
     *
     * @param closeHandler The handler to add.
     */
    public void addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        this.closeHandlers.add(closeHandler);
    }

    /**
     * Add a handler for inventory click events.
     *
     * @param clickHandler The handler to add.
     */
    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandlers.add(clickHandler);
    }

    /**
     * Open the inventory for a player.
     *
     * @param player The player to open the inventory for.
     */
    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    /**
     * Get the border slots of the inventory.
     *
     * @return The border slots.
     */
    public int[] getBorders() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> size < 27 || i < 9
                || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    /**
     * Get the corner slots of the inventory.
     *
     * @return The corner slots.
     */
    public int[] getCorners() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10)
                || i == 17 || i == size - 18
                || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
    }

    /**
     * Fill an entire row in the inventory with a specified item.
     *
     * @param row  The row number to fill (starting from 0).
     * @param item The item to fill the row with.
     */
    public void fillRow(int row, ItemStack item) {
        int start = row * 9;
        for (int i = start; i < start + 9; i++) {
            this.inventory.setItem(i, item);
        }
    }

    /**
     * Fill an entire column in the inventory with a specified item.
     *
     * @param col  The column number to fill (starting from 0).
     * @param item The item to fill the column with.
     */
    public void fillColumn(int col, ItemStack item) {
        for (int i = col; i < this.inventory.getSize(); i += 9) {
            this.inventory.setItem(i, item);
        }
    }

    /**
     * Fill the borders of the inventory with a specified item.
     *
     * @param item The item to fill the borders with.
     */
    public void fillBorders(ItemStack item) {
        for (int slot : getBorders()) {
            this.inventory.setItem(slot, item);
        }
    }

    /**
     * Fill the corners of the inventory with a specified item.
     *
     * @param item The item to fill the corners with.
     */
    public void fillCorners(ItemStack item) {
        for (int slot : getCorners()) {
            this.inventory.setItem(slot, item);
        }
    }

    /**
     * Handle inventory open events.
     *
     * @param e The inventory open event.
     */
    public void handleOpen(InventoryOpenEvent e) {
        onOpen(e);
        this.openHandlers.forEach(c -> c.accept(e));
    }

    /**
     * Handle inventory close events.
     *
     * @param e The inventory close event.
     * @return true if the inventory should be reopened, false otherwise.
     */
    public boolean handleClose(InventoryCloseEvent e) {
        onClose(e);
        this.closeHandlers.forEach(c -> c.accept(e));
        return this.closeFilter != null && this.closeFilter.test((Player) e.getPlayer());
    }

    /**
     * Handle inventory click events.
     *
     * @param e The inventory click event.
     */
    public void handleClick(InventoryClickEvent e) {
        onClick(e);
        this.clickHandlers.forEach(c -> c.accept(e));
        Consumer<InventoryClickEvent> clickConsumer = this.itemHandlers.get(e.getRawSlot());
        if (clickConsumer != null) {
            clickConsumer.accept(e);
        }
    }

    /**
     * Set the item handler for a specific slot.
     *
     * @param slot    The slot to set the handler for.
     * @param handler The click handler.
     */
    protected void setItemHandler(int slot, Consumer<InventoryClickEvent> handler) {
        if (handler != null) {
            this.itemHandlers.put(slot, handler);
        } else {
            this.itemHandlers.remove(slot);
        }
    }

    /**
     * Start updating the inventory content every tick.
     *
     * @param plugin         The plugin instance.
     * @param updateFunction The function to update the inventory.
     */
    public void startUpdating(Plugin plugin, Runnable updateFunction) {
        stopUpdating();
        this.updateTaskId = Bukkit.getScheduler().runTaskTimer(plugin, updateFunction, 0L, 1L).getTaskId();
    }

    /**
     * Stop updating the inventory content.
     */
    public void stopUpdating() {
        if (this.updateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(this.updateTaskId);
            this.updateTaskId = -1;
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
