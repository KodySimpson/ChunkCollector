package me.kodysimpson.chunkcollector.menusystem.menus;

import me.kodysimpson.chunkcollector.ChunkCollector;
import me.kodysimpson.chunkcollector.menusystem.PaginatedMenu;
import me.kodysimpson.chunkcollector.menusystem.PlayerMenuUtility;
import me.kodysimpson.chunkcollector.utils.Collector;
import me.kodysimpson.chunkcollector.utils.Database;
import me.kodysimpson.chunkcollector.utils.Utils;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.ArrayList;

public class CollectorStorageMenu extends PaginatedMenu {

    public CollectorStorageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        if (playerMenuUtility.getType() == Database.CollectionType.DROP){
            return "Collected Mob Drops";
        }else{
            return "Collected Crop Produce";
        }
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Collector collector = Database.findByID(playerMenuUtility.getCollectorID());
        ArrayList<ItemStack> drops = Utils.combine(collector.getItems());

        if (e.getCurrentItem().getType().equals(Material.BARRIER)) {

            new CollectorMenu(playerMenuUtility).open();

        }else if(e.getCurrentItem().getType().equals(Material.FIREWORK_ROCKET)){

            //ArrayList<TextComponent> receipt = new ArrayList<>();
            ComponentBuilder receipt = new ComponentBuilder(net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.BOLD + "Receipt of Items Sold");
            TextComponent newLine = new TextComponent(ComponentSerializer.parse("{text: \"\n\"}"));
            receipt.append(newLine).reset();

            long itemsSold = 0;
            double earned = 0.0;

            if (collector.getItems().isEmpty()){
                p.sendMessage(ChatColor.GRAY + "The collector is empty.");
            }else{
                //Sell each item
                if (playerMenuUtility.getType() == Database.CollectionType.DROP){
                    for (int i = 0; i < drops.size(); i++){

                        ChunkCollector.getEconomy().depositPlayer(playerMenuUtility.getOwner(), (Utils.getDropPricing(drops.get(i).getType()) * drops.get(i).getAmount()));

                        itemsSold = itemsSold + (collector.getSold() + drops.get(i).getAmount());
                        earned = earned + (collector.getEarned() + (Utils.getDropPricing(drops.get(i).getType()) * drops.get(i).getAmount()));

                        collector.setSold(collector.getSold() + drops.get(i).getAmount());
                        collector.setEarned(collector.getEarned() + (Utils.getDropPricing(drops.get(i).getType()) * drops.get(i).getAmount()));

                        receipt.append(new TextComponent(net.md_5.bungee.api.ChatColor.GRAY + "Sold " + drops.get(i).getAmount() + " " + drops.get(i).getType().toString().toLowerCase().replace("_", " ") + " for $" + net.md_5.bungee.api.ChatColor.YELLOW + String.format("%.2f", (Utils.getDropPricing(drops.get(i).getType()) * drops.get(i).getAmount())))).reset();
                        receipt.append(newLine);
                    }
                }else if (playerMenuUtility.getType() == Database.CollectionType.CROP){
                    for (int i = 0; i < drops.size(); i++){

                        ChunkCollector.getEconomy().depositPlayer(playerMenuUtility.getOwner(), (Utils.getCropPricing(drops.get(i).getType()) * drops.get(i).getAmount()));

                        itemsSold = itemsSold + (collector.getSold() + drops.get(i).getAmount());
                        earned = earned + (collector.getEarned() + (Utils.getDropPricing(drops.get(i).getType()) * drops.get(i).getAmount()));

                        collector.setSold(collector.getSold() + drops.get(i).getAmount());
                        collector.setEarned(collector.getEarned() + (Utils.getCropPricing(drops.get(i).getType()) * drops.get(i).getAmount()));

                        receipt.append(new TextComponent(net.md_5.bungee.api.ChatColor.GRAY + "Sold " + drops.get(i).getAmount() + " " + drops.get(i).getType().toString().toLowerCase().replace("_", " ") + " for $" + net.md_5.bungee.api.ChatColor.YELLOW + String.format("%.2f", (Utils.getCropPricing(drops.get(i).getType()) * drops.get(i).getAmount())))).reset();
                        receipt.append(newLine);
                    }
                }
                receipt.append(net.md_5.bungee.api.ChatColor.GREEN + "---------------------------");

                TextComponent text = new TextComponent(net.md_5.bungee.api.ChatColor.BLUE + "" + net.md_5.bungee.api.ChatColor.BOLD + "Hover for Receipt");
                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, receipt.create()));

                p.sendMessage(" ");
                p.sendMessage(ChatColor.GREEN + "All items have been sold.");
                p.sendMessage(ChatColor.GRAY + "Total Earned: " + ChatColor.GREEN + "$" + String.format("%.2f", earned));
                p.sendMessage(ChatColor.GRAY + "Total Sold: " + ChatColor.GREEN + itemsSold);

                p.spigot().sendMessage(text);

                //Clear all items from the collector, and then update it in the DB
                collector.getItems().clear();
                Database.updateCollector(collector);

                //Reload the page
                super.open();
            }

        }else if(e.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)){
            if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")){
                if (page == 0){
                    p.sendMessage(ChatColor.GRAY + "You are on the first page.");
                }else{
                    page = page - 1;
                    super.open();
                }
            }else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")){
                if (!((index + 1) >= drops.size())){
                    page = page + 1;
                    super.open();
                }else{
                    p.sendMessage(ChatColor.GRAY + "You are on the last page.");
                }
            }
        }
    }

    @Override
    public void setMenuItems() {

        addMenuBorder();

        Collector collector = Database.findByID(playerMenuUtility.getCollectorID());
        ArrayList<ItemStack> drops = Utils.combine(collector.getItems());

        if(drops != null && !drops.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= drops.size()) break;
                if (drops.get(index) != null){
                    inventory.addItem(drops.get(index));
                }
            }
        }


    }
}
