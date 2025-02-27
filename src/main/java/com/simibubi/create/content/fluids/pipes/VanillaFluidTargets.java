package com.simibubi.create.content.fluids.pipes;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LEVEL_HONEY;

import com.simibubi.create.AllFluids;

import io.github.fabricators_of_create.porting_lib.extensions.LevelExtensions;
import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

public class VanillaFluidTargets {

	public static boolean canProvideFluidWithoutCapability(BlockState state) {
		if (state.hasProperty(BlockStateProperties.LEVEL_HONEY))
			return true;
		if (state.is(Blocks.CAULDRON))
			return true;
		if (state.is(Blocks.LAVA_CAULDRON))
			return true;
		if (state.is(Blocks.WATER_CAULDRON))
			return true;
		return false;
	}

	public static FluidStack drainBlock(Level level, BlockPos pos, BlockState state, TransactionContext ctx) {
		if (state.hasProperty(BlockStateProperties.LEVEL_HONEY) && state.getValue(LEVEL_HONEY) >= 5) {
			((LevelExtensions) level).updateSnapshots(ctx);
			level.setBlock(pos, state.setValue(LEVEL_HONEY, 0), 3);
			return new FluidStack(AllFluids.HONEY.get()
				.getSource(), FluidConstants.BOTTLE);
		}

		if (state.is(Blocks.LAVA_CAULDRON)) {
			((LevelExtensions) level).updateSnapshots(ctx);
			level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
			return new FluidStack(Fluids.LAVA, FluidConstants.BUCKET);
		}

		if (state.is(Blocks.WATER_CAULDRON) && state.getBlock() instanceof LayeredCauldronBlock lcb) {
			if (!lcb.isFull(state))
				return FluidStack.EMPTY;
			((LevelExtensions) level).updateSnapshots(ctx);
			level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
			return new FluidStack(Fluids.WATER, FluidConstants.BUCKET);
		}

		return FluidStack.EMPTY;
	}

}
