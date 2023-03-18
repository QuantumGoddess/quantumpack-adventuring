package net.quantumgoddess.adventuring;

import net.fabricmc.api.ModInitializer;
import net.quantumgoddess.adventuring.entity.QuantumEntityType;
import net.quantumgoddess.adventuring.item.QuantumItems;
import net.quantumgoddess.adventuring.recipe.QuantumRecipeSerializer;
import net.quantumgoddess.adventuring.recipe.QuantumRecipeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdventuringMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("quantumadventuring");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		QuantumItems.registerAll();
		QuantumEntityType.registerAll();
		QuantumRecipeSerializer.registerAll();
		QuantumRecipeType.registerAll();
		LOGGER.info("Hello Fabric world!");

	}
}
