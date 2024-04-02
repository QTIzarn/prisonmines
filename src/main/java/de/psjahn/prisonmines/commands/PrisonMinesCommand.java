package de.psjahn.prisonmines.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import de.psjahn.prisonmines.PrisonMines;
import de.psjahn.prisonmines.util.Mine;
import de.psjahn.prisonmines.util.MineUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PrisonMinesCommand implements CommandExecutor, TabCompleter {
    private void printInvalidSyntaxNotice(CommandSender sender)
    {
        sender.sendMessage(PrisonMines.getPrefix()+ ChatColor.RED+"Invalid Syntax. Try doing: /prisonmines help");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        switch(args.length)
        {
            case 0 -> sender.sendMessage(PrisonMines.getPrefix()+"Running version " + PrisonMines.getPlugin().getDescription().getVersion());
            case 1 -> singleArgumentCommand(sender, args[0]);
            case 2 -> doubleArgumentCommand(sender, args);
            case 3 -> tripleArgumentCommand(sender, args);
            case 4 -> quadrupleArgumentCommand(sender, args);
            default -> printInvalidSyntaxNotice(sender);
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        switch(args.length)
        {
            case 1 -> { return Arrays.stream(new String[]{"help","list","reset","create","delete","rename","configure"}).toList(); }
            case 2 ->
            {
                switch(args[0].toLowerCase())
                {
                    case "rename", "delete", "configure" -> { return PrisonMines.getMines().stream().map(Mine::getName).toList(); }
                }
            }
            case 3 ->
            {
                switch(args[0].toLowerCase())
                {
                    case "configure" -> { return Arrays.stream(new String[]{"blocks","blockRarity","resetDelay"}).toList(); }
                }
            }
        }
        return null;
    }

    private void singleArgumentCommand(CommandSender sender, String arg) {
        switch(arg.toLowerCase())
        {
            case "help" -> printHelpInformation(sender);
            case "list" -> listMines(sender);
            case "reset" -> resetMines(sender);
            default -> printInvalidSyntaxNotice(sender);
        }
    }

    private void doubleArgumentCommand(CommandSender sender, String[] args) {
        switch(args[0].toLowerCase())
        {
            case "create" -> createNewMine(sender, args[1]);
            case "delete" -> deleteMine(sender, args[1]);
            default -> printInvalidSyntaxNotice(sender);
        }
    }

    private void tripleArgumentCommand(CommandSender sender, String[] args) {
        switch(args[0].toLowerCase())
        {
            case "rename" -> renameMine(sender, args[1], args[2]);
            default -> printInvalidSyntaxNotice(sender);
        }
    }

    private void quadrupleArgumentCommand(CommandSender sender, String[] args) {
        switch(args[0].toLowerCase())
        {
            case "configure" -> {
                switch(args[2].toLowerCase())
                {
                    case "blocks" -> configureMineBlocks(sender, args[1], args[3]);
                    case "blockRarity" -> configureBlockRarity(sender, args[1], args[3]);
                    case "resetDelay" -> configureResetDelay(sender, args[1], args[3]);
                    default -> printInvalidSyntaxNotice(sender);
                }
            }
            default -> printInvalidSyntaxNotice(sender);
        }
    }

    private void printHelpInformation(CommandSender sender)
    {
        if(sender instanceof Player)
        {
            sender.sendMessage(
                    ChatColor.GOLD+"/prisonmines "+ChatColor.GRAY+"→ Shows plugin information\n" +
                            ChatColor.GOLD+"/prisonmines help "+ChatColor.GRAY+"→ Shows help information\n" +
                            ChatColor.GOLD+"/prisonmines list "+ChatColor.GRAY+"→ Shows a list of all Mines\n" +
                            ChatColor.GOLD+"/prisonmines reset "+ChatColor.GRAY+"→ Resets all Mines\n" +
                            ChatColor.GOLD+"/prisonmines create <mine> "+ChatColor.GRAY+"→ Creates a new Mine using your current worldedit selection\n" +
                            ChatColor.GOLD+"/prisonmines delete <mine> "+ChatColor.GRAY+"→ Deletes a Mine\n" +
                            ChatColor.GOLD+"/prisonmines rename <oldName> <newName> "+ChatColor.GRAY+"→ Renames a Mine\n" +
                            ChatColor.GOLD+"/prisonmines configure <mine> blocks <block1,block2,..>\n" +
                            ChatColor.DARK_GRAY+"Example: /prisonmines configure mine1 blocks stone,coal_ore,iron_ore\n" +
                            ChatColor.GRAY+"→ Specifies which blocks will be spawned in the Mine\n" +
                            ChatColor.GOLD+"/prisonmines configure <mine> blockRarity <blockRarity1,blockRarity2,...>\n" +
                            ChatColor.DARK_GRAY+"Example: /prisonmines configure mine1 blockRarity 20,40,0.1\n" +
                            ChatColor.GRAY+"→ Specifies the chance of a block to spawn\n" +
                            ChatColor.GOLD+"/prisonmines configure <mine> resetDelay <delay>\n" +
                            ChatColor.GRAY+"→ Specifies how long it takes (in seconds) before a Mine resets");
        } else {
            sender.sendMessage(
                    ChatColor.GOLD+"/prisonmines "+ChatColor.GRAY+"Shows plugin information\n" +
                            ChatColor.GOLD+"/prisonmines help "+ChatColor.GRAY+"Shows help information\n" +
                            ChatColor.GOLD+"/prisonmines list "+ChatColor.GRAY+"Shows a list of all Mines\n" +
                            ChatColor.GOLD+"/prisonmines reset "+ChatColor.GRAY+"Resets all Mines\n" +
                            ChatColor.GOLD+"/prisonmines create <mine> "+ChatColor.GRAY+"Creates a new Mine using your current worldedit selection\n" +
                            ChatColor.GOLD+"/prisonmines delete <mine> "+ChatColor.GRAY+"Deletes a Mine\n" +
                            ChatColor.GOLD+"/prisonmines rename <oldName> <newName> "+ChatColor.GRAY+"Renames a Mine\n" +
                            ChatColor.GOLD+"/prisonmines configure <mine> blocks <block1,block2,..>\n" +
                            ChatColor.DARK_GRAY+"Example: /prisonmines configure mine1 blocks stone,coal_ore,iron_ore\n" +
                            ChatColor.GRAY+"Specifies which blocks will be spawned in the Mine\n" +
                            ChatColor.GOLD+"/prisonmines configure <mine> blockRarity <blockRarity1,blockRarity2,...>\n" +
                            ChatColor.DARK_GRAY+"Example: /prisonmines configure mine1 blockRarity 20,40,0.1\n" +
                            ChatColor.GRAY+"Specifies the chance of a block to spawn\n" +
                            ChatColor.GOLD+"/prisonmines configure <mine> resetDelay <delay>\n" +
                            ChatColor.GRAY+"Specifies how long it takes (in seconds) before a Mine resets");
        }
    }

    private void listMines(CommandSender sender)
    {
        if(PrisonMines.getMines().isEmpty())
        {
            sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"No Mines have been created.");
            return;
        }
        StringBuilder mines = new StringBuilder(ChatColor.GOLD + "Mines:");
        for(Mine m : PrisonMines.getMines())
        {
            mines.append("\n").append(ChatColor.GRAY).append(sender instanceof Player?"•":"-").append(ChatColor.GOLD).append(m.getName());
        }
        sender.sendMessage(mines.toString());
    }

    private static final List<Material> defaultMineBlocks = new ArrayList<>();
    static {
        defaultMineBlocks.add(Material.STONE);
    }

    private static final List<Double> defaultMineBlockRarities = new ArrayList<>();
    static {
        defaultMineBlockRarities.add(100.0);
    }

    private void createNewMine(CommandSender sender, String mineName)
    {
        if(PrisonMines.getWePlugin()==null)
        {
            sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"Creating Mines isn't possible without WorldEdit/FAWE being installed. Please install either one to create Mines.");
        }

        if(MineUtils.mineExists(mineName))
        {
            sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"There already exists a Mine with the same name.");
            return;
        }

        if(!(sender instanceof Player p))
        {
            sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"Please use this command in-game.");
            return;
        }

        try {
            World w = BukkitAdapter.adapt(Objects.requireNonNull(PrisonMines.getWePlugin().getSession(p).getSelection().getWorld()));
            Location minimumPoint = BukkitAdapter.adapt(w, PrisonMines.getWePlugin().getSession(p).getSelection().getMinimumPoint());
            Location maximumPoint = BukkitAdapter.adapt(w, PrisonMines.getWePlugin().getSession(p).getSelection().getMaximumPoint());
            Mine m = new Mine(mineName, minimumPoint, maximumPoint, defaultMineBlocks, defaultMineBlockRarities, 60, 0);
            PrisonMines.getMines().add(m);
        } catch (Exception e) {
            p.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"No WorldEdit selection has been made.");
        }
        p.sendMessage(PrisonMines.getPrefix()+ChatColor.GREEN+"New Mine \""+ChatColor.GRAY+mineName+ChatColor.GREEN+"\" has been created successfully.");
        p.sendMessage(PrisonMines.getPrefix()+ChatColor.GRAY+"(Continue the setup using the \"configure\" commands.)");
    }

    private void deleteMine(CommandSender sender, String mineName)
    {
        if(!MineUtils.mineExists(mineName))
        {
            sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"No Mine with the name \""+ChatColor.GRAY+mineName+ChatColor.GREEN+"\" exists.");
            return;
        }
        MineUtils.removeMine(mineName);
        sender.sendMessage(PrisonMines.getPrefix()+ChatColor.GREEN+"Deleted Mine \""+ChatColor.GRAY+mineName+ChatColor.GREEN+"\" Successfully.");
    }

    private void resetMines(CommandSender sender)
    {
        for(Mine m : PrisonMines.getMines())
        {
            m.setResetTimer(m.getResetDelay());
        }
        sender.sendMessage(PrisonMines.getPrefix()+ChatColor.GREEN+"Resetting Mines!");
    }

    private void renameMine(CommandSender sender, String oldName, String newName)
    {
        if(!MineUtils.mineExists(oldName))
        {
            sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"No Mine with the name \""+ChatColor.GRAY+oldName+ChatColor.GREEN+"\" exists.");
            return;
        }

        MineUtils.getMine(oldName).setName(newName);
        sender.sendMessage(PrisonMines.getPrefix()+ChatColor.GREEN+"Renamed Mine \""+ChatColor.GRAY+oldName+ChatColor.GREEN+"\" to \""+ChatColor.GRAY+newName+ChatColor.GREEN+"\" Successfully.");
    }

    private void configureMineBlocks(CommandSender sender, String mineName, String blocks)
    {
        if(!MineUtils.mineExists(mineName))
        {
            sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"No Mine with the name \""+ChatColor.GRAY+mineName+ChatColor.GREEN+"\" exists.");
            return;
        }

        Mine m = MineUtils.getMine(mineName);

        for(String s : blocks.split(","))
        {
            if(Material.matchMaterial(s)==null||!Material.matchMaterial(s).isBlock())
            {
                sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"Invalid Block Name: "+s);
                return;
            }
        }

        m.setBlocks(Arrays.stream(blocks.split(",")).map(Material::matchMaterial).toList());
        while(m.getBlockRarities().size()>m.getBlocks().size()) m.getBlockRarities().remove(m.getBlockRarities().size()-1);
        while(m.getBlockRarities().size()<m.getBlocks().size()) m.getBlockRarities().add(100.0);

        sender.sendMessage(PrisonMines.getPrefix()+ChatColor.GREEN+"Applied the specified Blocks Successfully.");
    }

    private void configureBlockRarity(CommandSender sender, String mineName, String values)
    {
        if(!MineUtils.mineExists(mineName))
        {
            sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"No Mine with the name \""+ChatColor.GRAY+mineName+ChatColor.GREEN+"\" exists.");
            return;
        }

        Mine m = MineUtils.getMine(mineName);

        if(values.split(",").length!=m.getBlocks().size())
        {
            sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"Incorrect amount of Values.");
            return;
        }

        for(String s : values.split(","))
        {
            try{ Double.parseDouble(s); } catch(Exception e)
            {
                sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"Invalid Value: "+s);
                return;
            }
        }

        m.setBlockRarities(Arrays.stream(values.split(",")).map(Double::parseDouble).toList());

        sender.sendMessage(PrisonMines.getPrefix()+ChatColor.GREEN+"Applied the specified Block Rarities Successfully.");
    }

    private void configureResetDelay(CommandSender sender, String mineName, String delay)
    {
        if(!MineUtils.mineExists(mineName))
        {
            sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"No Mine with the name \""+ChatColor.GRAY+mineName+ChatColor.GREEN+"\" exists.");
            return;
        }

        Mine m = MineUtils.getMine(mineName);

        try {
            m.setResetDelay(Integer.parseInt(delay));
        } catch(Exception e) {
            sender.sendMessage(PrisonMines.getPrefix()+ChatColor.RED+"Invalid Value.");
            return;
        }

        sender.sendMessage(PrisonMines.getPrefix()+ChatColor.GREEN+"Applied the reset delay Successfully.");
    }
}
