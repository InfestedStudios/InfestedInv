package org.infestedstudios.inv.templates;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.infestedstudios.inv.InfestedInv;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * InventoryTemplate allows creating and applying reusable inventory templates.
 */
public class InventoryTemplate {

    private final Map<Integer, ItemStack> templateItems = new HashMap<>();
    private final Map<Integer, Consumer<InventoryClickEvent>> clickHandlers = new HashMap<>();

    /**
     * Adds an item and its click handler to the template.
     *
     * @param slot    The slot where the item will be placed.
     * @param item    The item to place in the slot.
     * @param handler The click handler for the item.
     */
    public void addTemplateItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        templateItems.put(slot, item);
        clickHandlers.put(slot, handler);
    }

    /**
     * Applies the template to an inventory.
     *
     * @param inventory The inventory to apply the template to.
     */
    public void applyTemplate(InfestedInv inventory) {
        for (Map.Entry<Integer, ItemStack> entry : templateItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue(), clickHandlers.get(entry.getKey()));
        }
    }
}
