package net.quantumgoddess.adventuring.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.quantumgoddess.adventuring.client.render.entity.QuantumEntityRenderers;
import net.quantumgoddess.adventuring.client.render.model.QuantumEntityModelLayers;

@Environment(EnvType.CLIENT)
public class AdventuringClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        QuantumEntityRenderers.registerAll();
        QuantumEntityModelLayers.registerAll();
    }

}
