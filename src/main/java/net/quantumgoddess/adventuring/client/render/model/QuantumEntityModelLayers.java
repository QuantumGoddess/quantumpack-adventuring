package net.quantumgoddess.adventuring.client.render.model;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class QuantumEntityModelLayers {

    public static final EntityModelLayer DRYING_RACK = new EntityModelLayer(
            new Identifier("quantumadventuring", "drying_rack"), "main");;

    public static void registerAll() {
        EntityModelLayerRegistry.registerModelLayer(DRYING_RACK, DryingRackEntityModel::getTexturedModelData);
    }
}
