package net.nerdorg.vortexmod.util;

import net.minecraft.core.BlockPos;
import net.nerdorg.vortexmod.block.entity.TardisCoreBlockEntity;

public class CoreUtil {
    public static TardisCoreBlockEntity getClosestCoreBlockEntity(BlockPos blockPos) {
        TardisCoreBlockEntity closestCoreBlockEntity = null;
        double closestDistance = Double.MAX_VALUE;
        for (TardisCoreBlockEntity coreEntity : TardisCoreBlockEntity.coreEntities) {
            if (coreEntity.getLevel() != null && !coreEntity.isRemoved()) {
                BlockPos corePos = coreEntity.getBlockPos();
                double distance = corePos.distToCenterSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestCoreBlockEntity = coreEntity;
                }
            }
            else {
                TardisCoreBlockEntity.coreEntities.remove(coreEntity);
            }
        }

        return closestCoreBlockEntity;
    }
}
