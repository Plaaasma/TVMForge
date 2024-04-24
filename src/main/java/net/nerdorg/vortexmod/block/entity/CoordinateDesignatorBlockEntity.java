package net.nerdorg.vortexmod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.block.ModBlockEntities;

public class CoordinateDesignatorBlockEntity extends BlockEntity {

    public final ContainerData data;
    private int negative = 0;
    private int increment = 0;

    public CoordinateDesignatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COORDINATE_DESIGNATOR_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CoordinateDesignatorBlockEntity.this.negative;
                    case 1 -> CoordinateDesignatorBlockEntity.this.increment;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CoordinateDesignatorBlockEntity.this.negative = pValue;
                    case 1 -> CoordinateDesignatorBlockEntity.this.increment = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        CompoundTag vortexModData = pTag.getCompound(VortexMod.MODID);

        this.negative = vortexModData.getInt("negative");
        this.increment = vortexModData.getInt("increment");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        CompoundTag vortexModData = new CompoundTag();

        vortexModData.putInt("negative", this.negative);
        vortexModData.putInt("increment", this.increment);

        pTag.put(VortexMod.MODID, vortexModData);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) {
            return;
        }

        if (this.data.get(1) == 0) {
            this.data.set(1, 1);
        }

        setChanged(pLevel, pPos, pState);
    }
}
