package net.quantumgoddess.adventuring.client.render.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.quantumgoddess.adventuring.entity.QuantumEntityType;

public class QuantumEntityRenderers {

    public static void registerAll() {
        EntityRendererRegistry.register(QuantumEntityType.DRYING_RACK, DryingRackEntityRenderer::new);
    }
}
