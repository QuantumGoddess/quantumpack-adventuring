package net.quantumgoddess.adventuring.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.quantumgoddess.adventuring.entity.decoration.DryingRackEntity;

public class QuantumEntityType {

    public static final EntityType<DryingRackEntity> DRYING_RACK = FabricEntityTypeBuilder
            .<DryingRackEntity>create(SpawnGroup.MISC, DryingRackEntity::new)
            .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
            .trackRangeChunks(10).trackedUpdateRate(Integer.MAX_VALUE).build();

    public static void registerAll() {
        Registry.register(Registries.ENTITY_TYPE, new Identifier("quantumadventuring", "drying_rack"), DRYING_RACK);
    }

}
