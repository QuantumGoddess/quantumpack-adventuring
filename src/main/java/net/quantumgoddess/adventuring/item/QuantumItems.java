package net.quantumgoddess.adventuring.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.quantumgoddess.adventuring.entity.QuantumEntityType;

public class QuantumItems {
    public static final Item DRYING_RACK = new DryingRackItem(QuantumEntityType.DRYING_RACK, new FabricItemSettings());

    public static void registerAll() {
        Registry.register(Registries.ITEM, new Identifier("quantumadventuring", "drying_rack"), DRYING_RACK);
    }
}
