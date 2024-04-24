package net.nerdorg.vortexmod.datagen.providers;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, VortexMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.TARDIS_CORE.get())
                .add(ModBlocks.TARDIS_THROTTLE.get())
                .add(ModBlocks.TARDIS_COORDINATE_DESIGNATOR.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.TARDIS_THROTTLE.get())
                .add(ModBlocks.TARDIS_COORDINATE_DESIGNATOR.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.TARDIS_CORE.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.TARDIS_THROTTLE.get())
                .add(ModBlocks.TARDIS_COORDINATE_DESIGNATOR.get());
    }
}
