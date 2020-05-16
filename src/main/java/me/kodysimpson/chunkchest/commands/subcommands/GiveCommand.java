package me.kodysimpson.chunkchest.commands.subcommands;

import me.kodysimpson.chunkchest.ChunkCollector;
import me.kodysimpson.chunkchest.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class GiveCommand extends SubCommand {

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Give yourself or another player a chunk collector.";
    }

    @Override
    public String getSyntax() {
        return "/collector give [type] [player]";
    }

    @Override
    public void perform(Player p, String[] args) {

        if (args.length == 2){
            ItemStack collector = new ItemStack(Material.HOPPER, 1);
            ItemMeta collectorMeta = collector.getItemMeta();
            collectorMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mob Collector");

            collectorMeta.getPersistentDataContainer().set(new NamespacedKey(ChunkCollector.getPlugin(), "drop-collector"), PersistentDataType.STRING, "drop-collector");

            collector.setItemMeta(collectorMeta);

            p.getInventory().addItem(collector);
        }else if(args.length == 1){
            p.sendMessage("Specify a collector type. Types: mob or food");
            p.sendMessage("Example: /collector give mob");
        }


    }
}
