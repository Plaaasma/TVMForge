package net.nerdorg.vortexmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.block.custom.CoordinateDesignatorBlock;
import net.nerdorg.vortexmod.block.custom.TardisCoreBlock;
import net.nerdorg.vortexmod.block.custom.TardisThrottleBlock;
import net.nerdorg.vortexmod.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, VortexMod.MODID);

    public static final RegistryObject<Block> TARDIS_CORE = registerBlock("tardis_core",
            () -> new TardisCoreBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));

    public static final RegistryObject<Block> TARDIS_THROTTLE = registerBlock("tardis_throttle",
            () -> new TardisThrottleBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));

    public static final RegistryObject<Block> TARDIS_COORDINATE_DESIGNATOR = registerBlock("tardis_designator",
            () -> new CoordinateDesignatorBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noOcclusion()));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(1)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
