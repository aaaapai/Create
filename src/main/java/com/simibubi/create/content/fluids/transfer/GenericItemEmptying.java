package com.simibubi.create.content.fluids.transfer;

import java.util.List;
import java.util.Optional;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.foundation.utility.Pair;

import io.github.fabricators_of_create.porting_lib.transfer.MutableContainerItemContext;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public class GenericItemEmptying {

	private static final Container WRAPPER = new ItemStackHandlerContainer(1);

	public static boolean canItemBeEmptied(Level world, ItemStack stack) {
		if (PotionFluidHandler.isPotionItem(stack))
			return true;

		WRAPPER.setItem(0, stack);
		if (AllRecipeTypes.EMPTYING.find(WRAPPER, world)
			.isPresent())
			return true;

		return TransferUtil.getFluidContained(stack).isPresent();
	}

	public static Pair<FluidStack, ItemStack> emptyItem(Level world, ItemStack stack, boolean simulate) {
		FluidStack resultingFluid = FluidStack.EMPTY;
		ItemStack resultingItem = ItemStack.EMPTY;

		if (PotionFluidHandler.isPotionItem(stack))
			return PotionFluidHandler.emptyPotion(stack, simulate);

		WRAPPER.setItem(0, stack);
		Optional<Recipe<Container>> recipe = AllRecipeTypes.EMPTYING.find(WRAPPER, world);
		if (recipe.isPresent()) {
			EmptyingRecipe emptyingRecipe = (EmptyingRecipe) recipe.get();
			List<ItemStack> results = emptyingRecipe.rollResults();
			if (!simulate)
				stack.shrink(1);
			resultingItem = results.isEmpty() ? ItemStack.EMPTY : results.get(0);
			resultingFluid = emptyingRecipe.getResultingFluid();
			return Pair.of(resultingFluid, resultingItem);
		}

		ItemStack split = stack.copy();
		split.setCount(1);
		MutableContainerItemContext ctx = new MutableContainerItemContext(split);
		Storage<FluidVariant> tank = FluidStorage.ITEM.find(split, ctx);
		if (tank == null)
			return Pair.of(resultingFluid, resultingItem);
		try (Transaction t = TransferUtil.getTransaction()) {
			resultingFluid = TransferUtil.extractAnyFluid(tank, FluidConstants.BUCKET);
			resultingItem = ctx.getItemVariant().toStack((int) ctx.getAmount());
			if (!simulate) {
				stack.shrink(1);
				t.commit();
			}

			return Pair.of(resultingFluid, resultingItem);
		}
	}

}
