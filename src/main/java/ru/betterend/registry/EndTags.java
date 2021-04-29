package ru.betterend.registry;

import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;
import net.fabricmc.fabric.impl.tool.attribute.handlers.ModdedToolsVanillaBlocksToolHandler;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Named;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.material.Material;
import ru.betterend.BetterEnd;
import ru.betterend.blocks.EndTerrainBlock;
import ru.betterend.blocks.basis.PedestalBlock;
import ru.betterend.blocks.basis.SimpleLeavesBlock;
import ru.betterend.blocks.basis.VineBlock;
import ru.betterend.item.tool.EndHammerItem;
import ru.betterend.mixin.common.ComposterBlockAccessor;
import ru.betterend.util.TagHelper;

public class EndTags {
	// Table with common (c) tags:
	// https://fabricmc.net/wiki/tutorial:tags
	
	// Block Tags
	public static final Tag.Named<Block> BOOKSHELVES = makeCommonBlockTag("bookshelves");
	public static final Tag.Named<Block> GEN_TERRAIN = makeBlockTag("gen_terrain");
	public static final Tag.Named<Block> END_GROUND = makeBlockTag("end_ground");
	public static final Tag.Named<Block> PEDESTALS = makeBlockTag("pedestal");
	public static final Tag.Named<Block> BLOCK_CHEST = makeCommonBlockTag("chest");
	public static final Tag.Named<Block> END_STONES = makeCommonBlockTag("end_stones");
	public static final Tag.Named<Block> DRAGON_IMMUNE = getMCBlockTag("dragon_immune");
	
	// Item Tags
	public static final Tag.Named<Item> ITEM_CHEST = makeCommonItemTag("chest");
	public static final Tag.Named<Item> IRON_INGOTS = makeCommonItemTag("iron_ingots");
	public static final Tag.Named<Item> FURNACES = makeCommonItemTag("furnaces");
	public final static Tag.Named<Item> HAMMERS = makeFabricItemTag("hammers");

	public static <T> Tag.Named<T> makeTag(Supplier<TagCollection<T>> containerSupplier, ResourceLocation id) {
		Tag<T> tag = containerSupplier.get().getTag(id);
		return tag == null ? TagRegistry.create(id, containerSupplier) : (Named<T>) tag;
	}

	public static Tag.Named<Block> makeBlockTag(String name) {
		return makeTag(BlockTags::getAllTags, BetterEnd.makeID(name));
	}
	
	public static Tag.Named<Item> makeItemTag(String name) {
		return makeTag(ItemTags::getAllTags, BetterEnd.makeID(name));
	}
	
	public static Tag.Named<Block> makeCommonBlockTag(String name) {
		return makeTag(BlockTags::getAllTags, new ResourceLocation("c", name));
	}
	
	public static Tag.Named<Item> makeCommonItemTag(String name) {
		return makeTag(ItemTags::getAllTags, new ResourceLocation("c", name));
	}

	public static Tag.Named<Item> makeFabricItemTag(String name) {
		return makeTag(ItemTags::getAllTags, new ResourceLocation("fabric", name));
	}
	
	public static Tag.Named<Block> getMCBlockTag(String name) {
		ResourceLocation id = new ResourceLocation(name);
		Tag<Block> tag = BlockTags.getAllTags().getTag(id);
		return tag == null ? (Named<Block>) TagRegistry.block(id) : (Named<Block>) tag;
	}
	
	public static void register() {
		addSurfaceBlock(Blocks.END_STONE);
		addSurfaceBlock(EndBlocks.THALLASIUM.ore);
		addSurfaceBlock(EndBlocks.ENDSTONE_DUST);
		addSurfaceBlock(EndBlocks.AMBER_ORE);
		
		EndItems.getModBlocks().forEach(blockItem -> {
			Block block = ((BlockItem) blockItem).getBlock();
			if (block instanceof EndTerrainBlock) {
				addSurfaceBlock(block);
				TagHelper.addTag(BlockTags.NYLIUM, block);
			}
			else if (block instanceof LeavesBlock || block instanceof SimpleLeavesBlock) {
				TagHelper.addTag(BlockTags.LEAVES, block);
				ComposterBlockAccessor.callAdd(0.3F, block);
			}
			else if (block instanceof VineBlock) {
				TagHelper.addTag(BlockTags.CLIMBABLE, block);
			}
			else if (block instanceof PedestalBlock) {
				TagHelper.addTag(PEDESTALS, block);
			}
			
			Material mat = block.defaultBlockState().getMaterial();
			if (mat.equals(Material.PLANT) || mat.equals(Material.REPLACEABLE_PLANT)) {
				ComposterBlockAccessor.callAdd(0.1F, block);
			}
		});
		
		List<Item> hammers = Lists.newArrayList();
		EndItems.getModItems().forEach(item -> {
			if (item.isEdible()) {
				FoodProperties food = item.getFoodProperties();
				if (food != null) {
					float compost = food.getNutrition() * food.getSaturationModifier() * 0.18F;
					ComposterBlockAccessor.callAdd(compost, item);
				}
			}
			if (item instanceof EndHammerItem) {
				hammers.add(item);
			}
		});
		ToolManagerImpl.tag(HAMMERS).register(new ModdedToolsVanillaBlocksToolHandler(hammers));
		
		TagHelper.addTag(
			GEN_TERRAIN,
			EndBlocks.ENDER_ORE,
			EndBlocks.FLAVOLITE.stone,
			EndBlocks.VIOLECITE.stone,
			EndBlocks.SULPHURIC_ROCK.stone,
			EndBlocks.BRIMSTONE,
			EndBlocks.VIRID_JADESTONE.stone,
			EndBlocks.AZURE_JADESTONE.stone,
			EndBlocks.SANDY_JADESTONE.stone
		);
		TagHelper.addTag(END_GROUND, EndBlocks.SULPHURIC_ROCK.stone, EndBlocks.BRIMSTONE);
		
		TagHelper.addTag(FURNACES, Blocks.FURNACE);
		TagHelper.addTag(BlockTags.ANVIL, EndBlocks.AETERNIUM_ANVIL);
		
		TagHelper.addTag(BlockTags.BEACON_BASE_BLOCKS, EndBlocks.AETERNIUM_BLOCK);
		TagHelper.addTag(ItemTags.BEACON_PAYMENT_ITEMS, EndItems.AETERNIUM_INGOT);
		
		TagHelper.addTag(EndTags.DRAGON_IMMUNE, EndBlocks.ENDER_ORE, EndBlocks.ETERNAL_PEDESTAL, EndBlocks.FLAVOLITE_RUNED_ETERNAL, EndBlocks.FLAVOLITE_RUNED);
		
		TagHelper.addTag(EndTags.IRON_INGOTS, Items.IRON_INGOT);
		TagHelper.addTag(EndTags.IRON_INGOTS, EndBlocks.TERMINITE.ingot);
	}
	
	public static void addSurfaceBlock(Block block) {
		TagHelper.addTag(END_GROUND, block);
		TagHelper.addTag(GEN_TERRAIN, block);
	}
	
	public static void addTerrainTags(Registry<Biome> biomeRegistry) {
		biomeRegistry.forEach((biome) -> {
			if (biome.getBiomeCategory() == BiomeCategory.THEEND) {
				SurfaceBuilderConfiguration config = biome.getGenerationSettings().getSurfaceBuilderConfig();
				Block under = config.getUnderMaterial().getBlock();
				Block surface = config.getTopMaterial().getBlock();
				TagHelper.addTag(GEN_TERRAIN, under, surface);
				TagHelper.addTag(END_GROUND, surface);
			}
		});
		END_STONES.getValues().forEach(EndTags::addSurfaceBlock);
	}
}
