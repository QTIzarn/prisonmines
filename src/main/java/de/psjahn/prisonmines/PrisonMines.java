package de.psjahn.prisonmines;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.psjahn.prisonmines.commands.PrisonMinesCommand;
import de.psjahn.prisonmines.util.Mine;
import it.unimi.dsi.fastutil.Arrays;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class PrisonMines extends JavaPlugin {

    @Getter private static final List<Mine> mines = new ArrayList<>();

    @Getter private static PrisonMines plugin;
    @Getter private static final String prefix = "§7§l[§r§ePrison§6Mines§7§l]§r ";
    @Getter private static WorldEditPlugin wePlugin;

    private static ConsoleCommandSender log;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(Mine.class, "Mine");
        saveDefaultConfig();

        plugin = this;
        log = getServer().getConsoleSender();

        if(getServer().getPluginManager().getPlugin("WorldEdit")!=null)
        {
            wePlugin = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        } else {
            log(ChatColor.RED+"Warning! WorldEdit is not installed. The plugin will continue to work as intended, however creating new Mines isn't possible without it.");
        }

        log(ChatColor.GRAY+"Loading config...");

        try{
            mines.addAll((ArrayList<Mine>)getConfig().get("mines"));
        } catch(NullPointerException malformedConfiguration)
        {
            log(ChatColor.RED+"It seems there was a problem loading the configuration. Please make sure everything in the config is formatted correctly, and there exists a \"mines\" list in it.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        log(ChatColor.GREEN+"Finished config loading!");

        PrisonMinesCommand command = new PrisonMinesCommand();
        this.getCommand("prisonmines").setExecutor(command);
        this.getCommand("prisonmines").setTabCompleter(command);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for(Mine m : mines)
            {
                m.setResetTimer(m.getResetTimer()+1);
                if(m.getResetTimer()>=m.getResetDelay())
                {
                m.setResetTimer(0);

                Location minimum = m.getMinimumPosition();
                Location maximum = m.getMaximumPosition();
                World w = minimum.getWorld();
                for(int x = minimum.getBlockX(); x<=maximum.getBlockX(); x++)
                {
                    for(int y = minimum.getBlockY(); y<=maximum.getBlockY(); y++)
                    {
                        for(int z = minimum.getBlockZ(); z<=maximum.getBlockZ(); z++)
                        {
                            w.getBlockAt(x,y,z).setType(randomMaterial(m.getBlocks(), m.getBlockRarities()));
                        }
                    }
                }
                }
            }
        }, 20, 20);
    }

    public static Material randomMaterial(List<Material> materials, List<Double> chances) {
        double totalChance = 0.0f;
        for (double chance : chances) {
            totalChance += chance;
        }
        Random random = new Random();
        double randomValue = random.nextDouble() * totalChance;
        double accumulatedChance = 0.0f;
        for (int i = 0; i < materials.size(); i++) {
            accumulatedChance += chances.get(i);
            if (randomValue <= accumulatedChance) {
                return materials.get(i);
            }
        }
        return materials.get(materials.size() - 1);
    }

    private static void log(String msg)
    {
        log.sendMessage(prefix+msg);
    }

    @Override
    public void onDisable() {
        getConfig().set("mines", mines);
        saveConfig();
    }
}
