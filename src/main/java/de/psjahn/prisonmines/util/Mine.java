package de.psjahn.prisonmines.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@SerializableAs("Mine")
public class Mine implements Cloneable, ConfigurationSerializable {
    private String name;
    private Location minimumPosition;
    private Location maximumPosition;
    private List<Material> blocks;
    private List<Double> blockRarities;
    private int resetDelay;
    private int resetTimer;

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> output = new LinkedHashMap<>();

        output.put("name", name);
        output.put("minimum", minimumPosition);
        output.put("maximum", maximumPosition);
        output.put("blocks", blocks.stream().map(Enum::toString).toList());
        output.put("blockRarities", blockRarities);
        output.put("resetDelay", resetDelay);

        return output;
    }

    public static Mine deserialize(Map<String, Object> args) {
        List<String> blocksStringList = (ArrayList<String>)args.get("blocks");
        List<Material> blocksList = blocksStringList.stream().map(Material::valueOf).toList();

        return new Mine((String)args.get("name"), (Location)args.get("minimum"), (Location)args.get("maximum"), blocksList, (ArrayList<Double>)args.get("blockRarities"), (Integer)args.get("resetDelay"), 0);
    }
}
