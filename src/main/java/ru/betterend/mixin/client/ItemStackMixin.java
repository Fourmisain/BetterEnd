package ru.betterend.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import ru.betterend.events.ItemTooltipCallback;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(method = "getTooltipLines", at = @At("RETURN"))
	private void be_getTooltip(Player entity, TooltipFlag tooltipContext, CallbackInfoReturnable<List<Component>> info) {
		ItemTooltipCallback.EVENT.invoker().getTooltip(entity, ItemStack.class.cast(this), tooltipContext, info.getReturnValue());
	}
}
