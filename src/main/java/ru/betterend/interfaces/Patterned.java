package ru.betterend.interfaces;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import ru.betterend.BetterEnd;

public interface Patterned {
	//Blockstates
	public final static Identifier BLOCK_STATES_PATTERN = BetterEnd.makeID("patterns/blockstate/pattern_block.json");
	public final static Identifier SLAB_STATES_PATTERN = BetterEnd.makeID("patterns/blockstate/pattern_slab.json");
	public final static Identifier STAIRS_STATES_PATTERN = BetterEnd.makeID("patterns/blockstate/pattern_stairs.json");
	public final static Identifier WALL_STATES_PATTERN = BetterEnd.makeID("patterns/blockstate/pattern_wall.json");
	public final static Identifier BUTTON_STATES_PATTERN = BetterEnd.makeID("patterns/blockstate/pattern_button.json");
	public final static Identifier PILLAR_STATES_PATTERN = BetterEnd.makeID("patterns/blockstate/pattern_pillar.json");
	public final static Identifier PLATE_STATES_PATTERN = BetterEnd.makeID("patterns/blockstate/pattern_pressure_plate.json");
	public final static Identifier DOOR_STATES_PATTERN = BetterEnd.makeID("patterns/blockstate/pattern_door.json");
	public final static Identifier SAPLING_STATES_PATTERN = BetterEnd.makeID("patterns/blockstate/pattern_sapling.json");
	
	//Models Block
	public final static Identifier BASE_BLOCK_MODEL = BetterEnd.makeID("patterns/block/pattern_block.json");
	public final static Identifier SLAB_BLOCK_MODEL = BetterEnd.makeID("patterns/block/pattern_slab.json");
	public final static Identifier STAIRS_MODEL = BetterEnd.makeID("patterns/block/pattern_stairs.json");
	public final static Identifier STAIRS_MODEL_INNER = BetterEnd.makeID("patterns/block/pattern_inner_stairs.json");
	public final static Identifier STAIRS_MODEL_OUTER = BetterEnd.makeID("patterns/block/pattern_outer_stairs.json");
	public final static Identifier WALL_POST_MODEL = BetterEnd.makeID("patterns/block/pattern_wall_post.json");
	public final static Identifier WALL_SIDE_MODEL = BetterEnd.makeID("patterns/block/pattern_wall_side.json");
	public final static Identifier WALL_SIDE_TALL_MODEL = BetterEnd.makeID("patterns/block/pattern_wall_side_tall.json");
	public final static Identifier BUTTON_BLOCK_MODEL = BetterEnd.makeID("patterns/block/pattern_button.json");
	public final static Identifier BUTTON_PRESSED_MODEL = BetterEnd.makeID("patterns/block/pattern_button_pressed.json");
	public final static Identifier PILLAR_BLOCK_MODEL = BetterEnd.makeID("patterns/block/pattern_pillar.json");
	public final static Identifier PLATE_MODEL_UP = BetterEnd.makeID("patterns/block/pattern_pressure_plate_up.json");
	public final static Identifier PLATE_MODEL_DOWN = BetterEnd.makeID("patterns/block/pattern_pressure_plate_down.json");
	public final static Identifier DOOR_MODEL_TOP = BetterEnd.makeID("patterns/block/pattern_door_top.json");
	public final static Identifier DOOR_MODEL_TOP_HINGE = BetterEnd.makeID("patterns/block/pattern_door_top_hinge.json");
	public final static Identifier DOOR_MODEL_BOTTOM = BetterEnd.makeID("patterns/block/pattern_door_bottom.json");
	public final static Identifier DOOR_MODEL_BOTTOM_HINGE = BetterEnd.makeID("patterns/block/pattern_door_bottom_hinge.json");
	public final static Identifier SAPLING_MODEL = BetterEnd.makeID("patterns/block/pattern_sapling.json");
	
	//Models Item
	public final static Identifier WALL_ITEM_MODEL = BetterEnd.makeID("patterns/item/pattern_wall.json");
	public final static Identifier BUTTON_ITEM_MODEL = BetterEnd.makeID("patterns/item/pattern_button.json");
	public final static Identifier ITEM_MODEL = BetterEnd.makeID("patterns/item/pattern_item.json");

	default String getStatesPattern(Reader data, String name) {
		return null;
	}
	
	default String getModelPattern(String name) {
		return null;
	}
	
	default Identifier statePatternId() {
		return null;
	}
	
	default Identifier modelPatternId() {
		return null;
	}
	
	public static String createJson(Reader data, Identifier parent, String block) {
		try (BufferedReader buffer = new BufferedReader(data)) {
			return buffer.lines().collect(Collectors.joining())
					.replace("%parent%", parent.getPath())
					.replace("%block%", block);
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String createJson(Identifier patternId, Identifier parent, String block) {
		ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
		try (InputStream input = resourceManager.getResource(patternId).getInputStream()) {
			return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
					.lines().collect(Collectors.joining())
						.replace("%parent%", parent.getPath())
						.replace("%block%", block);
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String createJson(Identifier patternId, String texture) {
		ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
		try (InputStream input = resourceManager.getResource(patternId).getInputStream()) {
			return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
					.lines().collect(Collectors.joining())
					.replace("%texture%", texture);
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String createJson(Identifier patternId, Map<String, String> texturesMap, String block) {
		ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
		try (InputStream input = resourceManager.getResource(patternId).getInputStream()) {
			String json = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
					.lines().collect(Collectors.joining());
			for (Entry<String, String> entry : texturesMap.entrySet()) {
				json = json.replace(entry.getKey(), entry.getValue());
			}
			return json.replace("%block%", block);
		} catch (Exception ex) {
			return null;
		}
	}
}
