package net.nerdorg.vortexmod.block;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.block.entity.CoordinateDesignatorBlockEntity;
import net.nerdorg.vortexmod.block.entity.TardisCoreBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VortexMod.MODID);

    public static final RegistryObject<BlockEntityType<TardisCoreBlockEntity>> TARDIS_CORE_BE =
            BLOCK_ENTITIES.register("tardis_core_be", () ->
                    BlockEntityType.Builder.of(TardisCoreBlockEntity::new,
                            ModBlocks.TARDIS_CORE.get()).build(null));

    public static final RegistryObject<BlockEntityType<CoordinateDesignatorBlockEntity>> COORDINATE_DESIGNATOR_BE =
            BLOCK_ENTITIES.register("coordinate_designator_be", () ->
                    BlockEntityType.Builder.of(CoordinateDesignatorBlockEntity::new,
                            ModBlocks.TARDIS_COORDINATE_DESIGNATOR.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
