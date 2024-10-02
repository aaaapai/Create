package com.simibubi.create.compat.sodium;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.simibubi.create.Create;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.foundation.utility.Components;

import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

/**
 * Fixes the Mechanical Saw's sprite and lets players know when Indium isn't installed.
 */
public class SodiumCompat {
	private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

	public static final ResourceLocation SAW_TEXTURE = Create.asResource("block/saw_reversed");

	public static void init() {
		if (!Mods.INDIUM.isLoaded()) {
			ClientPlayConnectionEvents.JOIN.register(SodiumCompat::sendNoIndiumWarning);
		}

		boolean compatInitialized = false;

		Optional<ModContainer> containerOptional = FabricLoader.getInstance()
				.getModContainer(Mods.SODIUM.id());

		if (containerOptional.isPresent()) {
			Version sodiumVersion = containerOptional.get()
					.getMetadata()
					.getVersion();

			for (SpriteUtilCompat value : SpriteUtilCompat.values()) {
				if (value.doesWork.test(sodiumVersion)) {
					Minecraft mc = Minecraft.getInstance();
					WorldRenderEvents.START.register(ctx -> {
						Function<ResourceLocation, TextureAtlasSprite> atlas = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
						TextureAtlasSprite sawSprite = atlas.apply(SAW_TEXTURE);
						value.markSpriteAsActive.accept(sawSprite);
					});
					compatInitialized = true;
					break;
				}
			}
		}

		if (!compatInitialized) {
			Create.LOGGER.error("Create's Sodium compat errored and has been partially disabled. Report this!");
		}
	}

	public static void sendNoIndiumWarning(ClientPacketListener handler, PacketSender sender, Minecraft mc) {
		if (mc.player == null)
			return;

		MutableComponent text = ComponentUtils.wrapInSquareBrackets(Components.literal("WARN"))
				.withStyle(ChatFormatting.GOLD)
				.append(Components.literal(" Sodium is installed, but Indium is not. This will cause visual issues with Create!")
				)
				.withStyle(style -> style
						.withClickEvent(
								new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/indium")
						)
						.withHoverEvent(
								new HoverEvent(HoverEvent.Action.SHOW_TEXT, Components.literal("Click here to open Indium's mod page"))
						)
				);

		mc.player.displayClientMessage(text, false);
	}

	private enum SpriteUtilCompat {
		V0_5((version) -> {
			try {
				invokeOld(null);
				return true;
			} catch (Throwable ignored) {
				return false;
			}
		}, (sawSprite) -> {
			try {
				invokeOld(sawSprite);
			} catch (Throwable ignored) {}
		}),
		V0_6_API((version) -> {
			try {
				return VersionPredicate.parse(">=0.6.0-beta.3").test(version);
			} catch (VersionParsingException e) {
				return false;
			}
		}, (sawSprite) -> {
			try {
				SpriteUtil.INSTANCE.markSpriteActive(sawSprite);
			} catch (Throwable ignored) {}
		});

		private static MethodHandle markSpriteActiveHandle;

		private final Predicate<Version> doesWork;
		private final Consumer<TextureAtlasSprite> markSpriteAsActive;

		SpriteUtilCompat(Predicate<Version> doesWork, Consumer<TextureAtlasSprite> markSpriteAsActive) {
			this.doesWork = doesWork;
			this.markSpriteAsActive = markSpriteAsActive;
		}

		static {
			try {
				Class<?> spriteUtil = Class.forName("nme.jellysquid.mods.sodium.client.render.texture.SpriteUtil");

				MethodType methodType = MethodType.methodType(void.class, TextureAtlasSprite.class);
				markSpriteActiveHandle = lookup.findStatic(spriteUtil, "markSpriteActive", methodType);
			} catch (Throwable ignored) {}
		}

		public static void invokeOld(TextureAtlasSprite sawSprite) throws Throwable {
			markSpriteActiveHandle.invoke(sawSprite);
		}
	}
}
