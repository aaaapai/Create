package com.simibubi.create.foundation.data.recipe;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags.AllFluidTags;

import me.alphamode.forgetags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

public class CompactingRecipeGen extends ProcessingRecipeGen {

	GeneratedRecipe

	GRANITE = create("granite_from_flint", b -> b.require(Items.FLINT)
		.require(Items.FLINT)
		.require(Fluids.LAVA, FluidConstants.BUCKET / 10)
		.require(Items.RED_SAND)
		.output(Blocks.GRANITE, 1)),

		DIORITE = create("diorite_from_flint", b -> b.require(Items.FLINT)
			.require(Items.FLINT)
			.require(Fluids.LAVA, FluidConstants.BUCKET / 10)
			.require(Items.CALCITE)
			.output(Blocks.DIORITE, 1)),

		ANDESITE = create("andesite_from_flint", b -> b.require(Items.FLINT)
			.require(Items.FLINT)
			.require(Fluids.LAVA, FluidConstants.BUCKET / 10)
			.require(Items.GRAVEL)
			.output(Blocks.ANDESITE, 1)),

		CHOCOLATE = create("chocolate", b -> b.require(AllFluids.CHOCOLATE.get(), FluidConstants.BOTTLE)
			.output(AllItems.BAR_OF_CHOCOLATE.get(), 1)),

		BLAZE_CAKE = create("blaze_cake", b -> b.require(Tags.Items.EGGS)
			.require(Items.SUGAR)
			.require(AllItems.CINDER_FLOUR.get())
			.output(AllItems.BLAZE_CAKE_BASE.get(), 1)),

		HONEY = create("honey", b -> b.require(AllFluidTags.HONEY.tag, FluidConstants.BUCKET)
			.output(Items.HONEY_BLOCK, 1)),

		ICE = create("ice", b -> b
			.require(Blocks.SNOW_BLOCK).require(Blocks.SNOW_BLOCK).require(Blocks.SNOW_BLOCK)
			.require(Blocks.SNOW_BLOCK).require(Blocks.SNOW_BLOCK).require(Blocks.SNOW_BLOCK)
			.require(Blocks.SNOW_BLOCK).require(Blocks.SNOW_BLOCK).require(Blocks.SNOW_BLOCK)
			.output(Blocks.ICE))

	;

	public CompactingRecipeGen(FabricDataGenerator p_i48262_1_) {
		super(p_i48262_1_);
	}

	@Override
	protected AllRecipeTypes getRecipeType() {
		return AllRecipeTypes.COMPACTING;
	}

}
