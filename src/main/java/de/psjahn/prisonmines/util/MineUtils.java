package de.psjahn.prisonmines.util;

import de.psjahn.prisonmines.PrisonMines;

public class MineUtils {
    public static boolean mineExists(String name)
    {
        return PrisonMines.getMines().stream().anyMatch(m->m.getName().equals(name));
    }

    public static Mine getMine(String name)
    {
        return PrisonMines.getMines().stream().filter(m->m.getName().equals(name)).findFirst().get();
    }

    public static int getMineIndex(String name)
    {
        return PrisonMines.getMines().indexOf(getMine(name));
    }

    public static void removeMine(String name)
    {
        PrisonMines.getMines().remove(getMineIndex(name));
    }
}
