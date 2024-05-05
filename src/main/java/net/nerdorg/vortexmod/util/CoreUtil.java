package net.nerdorg.vortexmod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.nerdorg.vortexmod.block.entity.TardisCoreBlockEntity;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CoreUtil {
    public static List<TardisCoreBlockEntity> coreEntities = new ArrayList<>();
    public static HashMap<ServerPlayer, Vector3d> hangingPlayers = new HashMap<>();

    public static TardisCoreBlockEntity getClosestCoreBlockEntity(BlockPos blockPos) {
        if (coreEntities == null) {
            coreEntities = new ArrayList<>();
        }
        TardisCoreBlockEntity closestCoreBlockEntity = null;
        double closestDistance = Double.MAX_VALUE;
        Iterator<TardisCoreBlockEntity> iterator = coreEntities.iterator();

        while (iterator.hasNext()) {
            TardisCoreBlockEntity coreEntity = iterator.next();
            if (coreEntity.getLevel() != null && !coreEntity.isRemoved()) {
                BlockPos corePos = coreEntity.getBlockPos();
                double distance = corePos.distToCenterSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestCoreBlockEntity = coreEntity;
                }
            } else {
                iterator.remove();  // Safe removal while iterating
            }
        }

        return closestCoreBlockEntity;
    }

}
