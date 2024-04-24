package net.nerdorg.vortexmod.datagen.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.block.ModBlocks;
import net.nerdorg.vortexmod.block.custom.CoordinateDesignatorBlock;
import net.nerdorg.vortexmod.block.custom.TardisCoreBlock;
import net.nerdorg.vortexmod.block.custom.TardisThrottleBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, VortexMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(ModBlocks.TARDIS_CORE.get())
                .forAllStates(blockState -> {
                    String model = "tardis_core";
                    return ConfiguredModel.builder()
                            .modelFile(new ModelFile.UncheckedModelFile(modLoc("block/" + model)))
                            .rotationX(blockState.getValue(BlockStateProperties.ATTACH_FACE).ordinal() * 90)
                            .rotationY((((int) blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) + (blockState.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING ? 180 : 0)) % 360)
                            .build();
                });
        simpleBlockItem(ModBlocks.TARDIS_CORE.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/tardis_core")));

        getVariantBuilder(ModBlocks.TARDIS_THROTTLE.get())
                .forAllStates(blockState -> {
                    boolean powered = blockState.getValue(TardisThrottleBlock.POWERED);
                    String model = powered ? "tardis_throttle" : "tardis_throttle_powered";
                    return ConfiguredModel.builder()
                            .modelFile(new ModelFile.UncheckedModelFile(modLoc("block/" + model)))
                            .rotationX(blockState.getValue(BlockStateProperties.ATTACH_FACE).ordinal() * 90)
                            .rotationY((((int) blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) + (blockState.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING ? 180 : 0)) % 360)
                            .build();
                });
        simpleBlockItem(ModBlocks.TARDIS_THROTTLE.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/tardis_throttle_powered")));

        getVariantBuilder(ModBlocks.TARDIS_COORDINATE_DESIGNATOR.get())
                .forAllStates(blockState -> {
                    int increment_step = blockState.getValue(CoordinateDesignatorBlock.INCREMENT) + 1;
                    String model = "desig_state" + increment_step;
                    return ConfiguredModel.builder()
                            .modelFile(new ModelFile.UncheckedModelFile(modLoc("block/coord_desig_states/" + model)))
                            .rotationX(blockState.getValue(BlockStateProperties.ATTACH_FACE).ordinal() * 90)
                            .rotationY((((int) blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) + (blockState.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING ? 180 : 0)) % 360)
                            .build();
                });
        simpleBlockItem(ModBlocks.TARDIS_COORDINATE_DESIGNATOR.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/coord_desig_states/desig_state1")));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
