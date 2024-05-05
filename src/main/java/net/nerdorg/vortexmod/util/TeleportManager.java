package net.nerdorg.vortexmod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.nerdorg.vortexmod.block.entity.TardisCoreBlockEntity;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

public class TeleportManager {
    public static void teleportShipWithDimChange(TardisCoreBlockEntity tardisCoreBlockEntity, ServerLevel targetLevel, double x, double y, double z, boolean clear, List<BlockPos> newPositions, Vector3dc shipPos) {
        ServerLevel currentLevel = (ServerLevel) tardisCoreBlockEntity.getLevel();
        MinecraftServer server = currentLevel.getServer();

        // Load chunks temporarily at where we're leaving to ensure everything gets teleported.
        ChunkPos chunkpos = new ChunkPos(BlockPos.containing(tardisCoreBlockEntity.getBlockPos().getX(), tardisCoreBlockEntity.getBlockPos().getY(), tardisCoreBlockEntity.getBlockPos().getZ()));
        currentLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 0, -1);
        currentLevel.getChunk(chunkpos.x, chunkpos.z);

        // Load chunks temporarily in the new location to ensure everything gets teleported.
        chunkpos = new ChunkPos(BlockPos.containing(x, y, z));
        targetLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 0, -1);
        targetLevel.getChunk(chunkpos.x, chunkpos.z);

        AABB newWorldAABB = AABBUtil.createAABBFromPositions(newPositions);

        AABB offsetFromCenter = new AABB(new BlockPos((int) (shipPos.x() - newWorldAABB.minX), (int) (shipPos.y() - newWorldAABB.minY), (int) (shipPos.z() - newWorldAABB.minZ)), new BlockPos((int) (shipPos.x() - newWorldAABB.maxX), (int) (shipPos.y() - newWorldAABB.maxY), (int) (shipPos.z() - newWorldAABB.maxZ)));

        if (clear) {
            AABB areaToClear = new AABB(new BlockPos((int) (x + offsetFromCenter.minX - 10), (int) (y + offsetFromCenter.minY - 5), (int) (z + offsetFromCenter.minZ - 10)), new BlockPos((int) (x + offsetFromCenter.maxX + 10), (int) (y + offsetFromCenter.maxY + 5), (int) (z + offsetFromCenter.maxZ + 10)));

            for (double scan_x = areaToClear.minX; scan_x <= areaToClear.maxX; scan_x++) {
                for (double scan_y = areaToClear.minY; scan_y <= areaToClear.maxY; scan_y++) {
                    for (double scan_z = areaToClear.minZ; scan_z <= areaToClear.maxZ; scan_z++) {
                        if (!targetLevel.getBlockState((new BlockPos((int) scan_x, (int) scan_y, (int) scan_z))).is(Blocks.AIR)) {
                            targetLevel.removeBlock(new BlockPos((int) scan_x, (int) scan_y, (int) scan_z), false);
                        }
                    }
                }
            }
        }

        for (BlockPos currentPos : newPositions) {
            BlockState stateAtPos = currentLevel.getBlockState(currentPos);

            double xOffset = currentPos.getX() - newWorldAABB.getCenter().x();
            double yOffset = currentPos.getY() - newWorldAABB.getCenter().y();
            double zOffset = currentPos.getZ() - newWorldAABB.getCenter().z();

            BlockPos targetPos = new BlockPos(
                    (int) Math.round(x + xOffset),
                    (int) Math.round(y + yOffset),
                    (int) Math.round(z + zOffset)
            );

            targetLevel.setBlock(targetPos, stateAtPos, 3 | 16);

            BlockEntity blockEntity = currentLevel.getBlockEntity(currentPos);

            CompoundTag nbtData = null;
            if (blockEntity != null) {
                nbtData = blockEntity.saveWithFullMetadata();
                if (blockEntity instanceof BaseContainerBlockEntity containerBlockEntity) {
                    for (int slot = 0; slot < containerBlockEntity.getContainerSize(); slot++) {
                        containerBlockEntity.setItem(slot, new ItemStack(Items.AIR));
                    }
                }
            }

            if (nbtData != null) {
                BlockEntity newBlockEntity = targetLevel.getBlockEntity(targetPos);
                if (newBlockEntity != null) {
                    newBlockEntity.load(nbtData);
                }
            }

            currentLevel.setBlock(currentPos, Blocks.AIR.defaultBlockState(), 3 | 16);
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Vec3 playerPos = player.position();

            Vec3 shipOffset = player.position().subtract(newWorldAABB.getCenter());

            if (newWorldAABB.contains(playerPos)) {
                Vector3d teleportPos = new Vector3d(x + shipOffset.x(), y + shipOffset.y() + 0.1, z + shipOffset.z());
                player.teleportTo(targetLevel,
                        teleportPos.x(), teleportPos.y(), teleportPos.z(),
                        RelativeMovement.ALL,
                        player.getYHeadRot(),
                        player.getXRot());

                CoreUtil.hangingPlayers.put(player, teleportPos);
            }
        }
    }

    public static void teleportShipWithDimChange(ServerShip serverShip, ServerLevel currentLevel, ServerLevel targetLevel, double x, double y, double z, boolean clear) {
        AABBdc preTpAABB = serverShip.getWorldAABB();
        Vector3dc preTpPos = serverShip.getTransform().getPositionInWorld();

        ShipTeleportDataImpl shipTeleportData = new ShipTeleportDataImpl(
                new Vector3d(x, y, z),
                serverShip.getTransform().getShipToWorldRotation(),
                serverShip.getVelocity(),
                serverShip.getOmega(),
                VSGameUtilsKt.getDimensionId(targetLevel),
                1.0
        );

        // Load chunks temporarily at where we're leaving to ensure everything gets teleported.
        ChunkPos chunkpos = new ChunkPos(BlockPos.containing(preTpPos.x(), preTpPos.y(), preTpPos.z()));
        currentLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 0, -1);
        currentLevel.getChunk(chunkpos.x, chunkpos.z);

        // Load chunks temporarily in the new location to ensure everything gets teleported.
        chunkpos = new ChunkPos(BlockPos.containing(x, y, z));
        targetLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 0, -1);
        targetLevel.getChunk(chunkpos.x, chunkpos.z);

        AABB offsetFromCenter = new AABB(new BlockPos((int) (preTpPos.x() - preTpAABB.minX()), (int) (preTpPos.y() - preTpAABB.minY()), (int) (preTpPos.z() - preTpAABB.minZ())), new BlockPos((int) (preTpPos.x() - preTpAABB.maxX()), (int) (preTpPos.y() - preTpAABB.maxY()), (int) (preTpPos.z() - preTpAABB.maxZ())));

        if (clear) {
            AABB areaToClear = new AABB(new BlockPos((int) (x + offsetFromCenter.minX - 10), (int) (y + offsetFromCenter.minY - 5), (int) (z + offsetFromCenter.minZ - 10)), new BlockPos((int) (x + offsetFromCenter.maxX + 10), (int) (y + offsetFromCenter.maxY + 5), (int) (z + offsetFromCenter.maxZ + 10)));

            boolean hasOtherThanAir = false;
            for (double scan_x = areaToClear.minX; scan_x <= areaToClear.maxX; scan_x++) {
                for (double scan_y = areaToClear.minY; scan_y <= areaToClear.maxY; scan_y++) {
                    for (double scan_z = areaToClear.minZ; scan_z <= areaToClear.maxZ; scan_z++) {
                        if (!targetLevel.getBlockState((new BlockPos((int) scan_x, (int) scan_y, (int) scan_z))).is(Blocks.AIR)) {
                            targetLevel.removeBlock(new BlockPos((int) scan_x, (int) scan_y, (int) scan_z), false);
                            hasOtherThanAir = true;
                        }
                    }
                }
            }
            if (hasOtherThanAir) {
                return;
            }
        }

        VSGameUtilsKt.getShipObjectWorld(currentLevel).teleportShip(serverShip, shipTeleportData);
    }

    public static void teleportShip(TardisCoreBlockEntity tardisCoreBlockEntity, double x, double y, double z) {
        ServerLevel currentLevel = (ServerLevel) tardisCoreBlockEntity.getLevel();
        MinecraftServer server = currentLevel.getServer();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Vec3 playerPos = player.position();
            Vector3dc playerPosVS = new Vector3d(playerPos.x(), playerPos.y(), playerPos.z());

            Vector3d shipOffset = new Vector3d(0, 0, 0);
            tardisCoreBlockEntity.getServerShip().getTransform().getPositionInWorld().sub(playerPosVS, shipOffset);

            if (tardisCoreBlockEntity.getServerShip().getWorldAABB().containsPoint(playerPosVS)) {
                Vector3d teleportPos = new Vector3d(x - shipOffset.x(), y - shipOffset.y() + 0.1, z - shipOffset.z());
                player.setNoGravity(true);
                player.teleportTo(currentLevel,
                        teleportPos.x(), teleportPos.y(), teleportPos.z(),
                        RelativeMovement.ALL,
                        player.getYHeadRot(),
                        player.getXRot());
                player.setNoGravity(true);
                if (!CoreUtil.hangingPlayers.containsKey(player)) {
                    CoreUtil.hangingPlayers.put(player, teleportPos);
                }
            }
        }

        ShipTeleportDataImpl shipTeleportData = new ShipTeleportDataImpl(
                new Vector3d(x, y, z),
                tardisCoreBlockEntity.getServerShip().getTransform().getShipToWorldRotation(),
                tardisCoreBlockEntity.getServerShip().getVelocity(),
                tardisCoreBlockEntity.getServerShip().getOmega(),
                VSGameUtilsKt.getDimensionId(currentLevel),
                1.0
        );
        VSGameUtilsKt.getShipObjectWorld(currentLevel).teleportShip(tardisCoreBlockEntity.getServerShip(), shipTeleportData);
    }
}
