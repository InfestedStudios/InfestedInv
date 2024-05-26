package org.infestedstudios.inv.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.infestedstudios.inv.InfestedInv;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manager for InfestedInv listeners.
 */
public final class InfestedInvManager {

    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

    private InfestedInvManager() {
        throw new UnsupportedOperationException();
    }

    /**
     * Register listeners for InfestedInv.
     *
     * @param plugin plugin to register
     * @throws NullPointerException if plugin is null
     * @throws IllegalStateException if InfestedInv is already registered
     */
    public static void register(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        if (REGISTERED.getAndSet(true)) {
            throw new IllegalStateException("InfestedInv is already registered");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
    }

    /**
     * Close all open InfestedInv inventories.
     */
    public static void closeAll() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getOpenInventory().getTopInventory().getHolder() instanceof InfestedInv)
                .forEach(Player::closeInventory);
    }

    public static final class InventoryListener implements Listener {

        private final Plugin plugin;

        public InventoryListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (e.getInventory().getHolder() instanceof InfestedInv && e.getClickedInventory() != null) {
                InfestedInv inv = (InfestedInv) e.getInventory().getHolder();

                boolean wasCancelled = e.isCancelled();
                e.setCancelled(true);

                inv.handleClick(e);

                // This prevents un-canceling the event if another plugin canceled it before
                if (!wasCancelled && !e.isCancelled()) {
                    e.setCancelled(false);
                }
            }
        }

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent e) {
            if (e.getInventory().getHolder() instanceof InfestedInv) {
                InfestedInv inv = (InfestedInv) e.getInventory().getHolder();

                inv.handleOpen(e);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            if (e.getInventory().getHolder() instanceof InfestedInv) {
                InfestedInv inv = (InfestedInv) e.getInventory().getHolder();

                if (inv.handleClose(e)) {
                    Bukkit.getScheduler().runTask(this.plugin, () -> inv.open((Player) e.getPlayer()));
                }
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent e) {
            if (e.getPlugin() == this.plugin) {
                closeAll();

                REGISTERED.set(false);
            }
        }
    }
}
