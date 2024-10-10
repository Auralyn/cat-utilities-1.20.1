package arson.lynn.cat;

import arson.lynn.cat.block.ModBlocks;
import arson.lynn.cat.item.ModItemGroups;
import arson.lynn.cat.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatUtilities implements ModInitializer {
	public static final String MOD_ID = "cat-utilities";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		LOGGER.info("Cat Utilities has started!");
	}
}