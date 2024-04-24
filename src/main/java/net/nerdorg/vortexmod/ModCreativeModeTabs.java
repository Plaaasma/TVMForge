package net.nerdorg.vortexmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.nerdorg.vortexmod.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VortexMod.MODID);

    public static final RegistryObject<CreativeModeTab> VORTEX_MAIN_TAB = CREATIVE_MODE_TABS.register("vortex_main_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.TARDIS_CORE.get()))
                    .title(Component.translatable("creativetab.vortex_main_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.TARDIS_CORE.get());
                        pOutput.accept(ModBlocks.TARDIS_THROTTLE.get());
                        pOutput.accept(ModBlocks.TARDIS_COORDINATE_DESIGNATOR.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
