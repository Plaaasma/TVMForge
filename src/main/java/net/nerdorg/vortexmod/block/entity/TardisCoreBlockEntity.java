package net.nerdorg.vortexmod.block.entity;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.block.ModBlockEntities;
import net.nerdorg.vortexmod.contraption.ModForceApplier;
import net.nerdorg.vortexmod.contraption.Stabilize;
import net.nerdorg.vortexmod.util.AABBUtil;
import net.nerdorg.vortexmod.util.CoreUtil;
import net.nerdorg.vortexmod.util.ForceManager;
import net.nerdorg.vortexmod.util.TeleportManager;
import net.nerdorg.vortexmod.worldgen.dimension.ModDimensions;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.primitives.AABBdc;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.*;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.apigame.world.IPlayer;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;
import org.valkyrienskies.core.impl.config.VSCoreConfig;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.core.impl.game.ships.ShipObject;
import org.valkyrienskies.core.impl.shadow.B;
import org.valkyrienskies.core.impl.shadow.S;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.physics_api.PhysicsBodyReference;

import java.util.ArrayList;
import java.util.List;

import static org.valkyrienskies.mod.common.assembly.ShipAssemblyKt.createNewShipWithBlocks;

public class TardisCoreBlockEntity extends BlockEntity {
    private BlockPos targetPos = new BlockPos(0, 0, 0);
    private ServerShip serverShip;
    private ModForceApplier forceApplier;
    private boolean moveToTarget;
    private int time;

    public TardisCoreBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TARDIS_CORE_BE.get(), pPos, pBlockState);
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
        int targetX = vortexModData.getInt("targetX");
        int targetY = vortexModData.getInt("targetY");
        int targetZ = vortexModData.getInt("targetZ");
        this.targetPos = new BlockPos(targetX, targetY, targetZ);
        this.moveToTarget = vortexModData.getBoolean("moveToTarget");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        CompoundTag vortexModData = new CompoundTag();
        vortexModData.putInt("targetX", this.targetPos.getX());
        vortexModData.putInt("targetY", this.targetPos.getY());
        vortexModData.putInt("targetZ", this.targetPos.getZ());

        vortexModData.putBoolean("moveToTarget", this.moveToTarget);

        pTag.put(VortexMod.MODID, vortexModData);
    }

    public BlockPos offsetTarget(int x, int y, int z) {
        this.targetPos = this.targetPos.offset(x, y, z);
        return this.targetPos;
    }

    public void setMoveToTarget(boolean moveToTarget) {
        this.moveToTarget = moveToTarget;
    }

    public boolean shouldMoveToTarget() {
        return this.moveToTarget;
    }

    public BlockPos getTargetPos() {
        return this.targetPos;
    }

    public void setTargetPos(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    public ServerShip getServerShip() {
        return this.serverShip;
    }

    public ModForceApplier getForceApplier() {
        return this.forceApplier;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel.isClientSide()) {
            return;
        }

        MinecraftServer server = this.level.getServer();

        if (this.serverShip == null || this.forceApplier == null) {
            updateShipReference((ServerLevel) pLevel, pPos);
        }
        else {
            if (this.hasPassenger() && this.time > 30) {
                this.serverShip.setStatic(false);
            }
            else {
                this.serverShip.setStatic(true);
            }
            if (this.moveToTarget) {
                Vector3dc shipPos = this.serverShip.getTransform().getPositionInWorld();
                double shipVelocity2d = Math.sqrt(this.serverShip.getVelocity().x() * this.serverShip.getVelocity().x() + this.serverShip.getVelocity().z() * this.serverShip.getVelocity().z());

                if (this.level.dimensionTypeId() != ModDimensions.vortex_DIM_TYPE) {
                    if (this.time > 30) {
                        this.serverShip.setStatic(false);
                        givePassengersGravity();
                        ForceManager.forceTowardsTarget(this.forceApplier, this);
                    }
                    else {
                        ForceManager.doVerticleForceToTarget(this.forceApplier, this);
                    }
                    if (shipPos.distance(this.targetPos.getX(), shipPos.y(), this.targetPos.getZ()) > 50) {
                        TeleportManager.teleportShipWithDimChange(this, server.getLevel(ModDimensions.vortexDIM_LEVEL_KEY), shipPos.x(), -57, shipPos.z(), true, disassemble(), shipPos);
                    }
                }
                else {
                    clearAreaNearShip();
                    givePassengersGravity();
                    if (this.time > 30) {
                        this.serverShip.setStatic(false);
                        givePassengersGravity();
                        ForceManager.forceTowardsTarget(this.forceApplier, this, this.targetPos.getX(), -57, this.targetPos.getZ());
                    }
                    else {
                        ForceManager.doVerticleForceToTarget(this.forceApplier, this, this.targetPos.getX(), -57, this.targetPos.getZ());
                    }
                    if (shipPos.distance(this.targetPos.getX(), shipPos.y(), this.targetPos.getZ()) < 20) {
                        TeleportManager.teleportShipWithDimChange(this, server.overworld(), this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), false, disassemble(), shipPos);
                    }
                }
            }
            else {
                this.serverShip.setStatic(false);
            }
        }

        if (!CoreUtil.coreEntities.contains(this)) {
            CoreUtil.coreEntities.add(this);
        }

        this.time++;

        setChanged(pLevel, pPos, pState);
    }

    public void assemble(ServerLevel level) {
        DenseBlockPosSet denseBlockPosSet = new DenseBlockPosSet();

        for (int x = -2; x < 3; x++) {
            for (int y = -1; y < 4; y++) {
                for (int z = -2; z < 3; z++) {
                    BlockPos offsetPos = this.getBlockPos().offset(x, y, z);
                    denseBlockPosSet.add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
                }
            }
        }

        ServerShip serverShip = createNewShipWithBlocks(this.getBlockPos(),
                denseBlockPosSet,
                level);

        serverShip.saveAttachment(ModForceApplier.class, new ModForceApplier());

        serverShip.setStatic(true);

        this.serverShip = serverShip;

        MinecraftServer server = this.level.getServer();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Vec3 playerPos = player.position();
            Vector3dc playerPosVS = new Vector3d(playerPos.x(), playerPos.y(), playerPos.z());

            Vector3d shipOffset = new Vector3d(0, 0, 0);
            this.serverShip.getTransform().getPositionInWorld().sub(playerPosVS, shipOffset);

            if (this.serverShip.getWorldAABB().containsPoint(playerPosVS)) {
                player.teleportTo(playerPos.x(), playerPos.y() + 0.5, playerPos.z());
            }
        }
    }

    public List<BlockPos> disassemble() {
        ServerShip ship = this.serverShip;

        ship.setStatic(true);

        AABBic shipAABB = ship.getShipAABB();
        AABBdc worldAABB = ship.getWorldAABB();

        int offsetX = (int) (worldAABB.minX() - 0.5) - (int) shipAABB.minX();
        int offsetY = (int) (worldAABB.minY() + 0.5) - (int) shipAABB.minY();
        int offsetZ = (int) worldAABB.minZ() - (int) shipAABB.minZ();

        List<BlockPos> newBlockPositions = new ArrayList<>();

        for (int x = (int) shipAABB.minX(); x < shipAABB.maxX(); x++) {
            for (int y = (int) shipAABB.minY(); y < shipAABB.maxY() + 1; y++) {
                for (int z = (int) shipAABB.minZ(); z < shipAABB.maxZ(); z++) {
                    BlockPos shipPos = new BlockPos(x, y, z);
                    // Apply the offsets to calculate the correct world position
                    BlockPos worldPos = new BlockPos(x + offsetX, y + offsetY, z + offsetZ);
                    BlockState shipState = level.getBlockState(shipPos);

                    if (shipState.isAir()) {
                        continue;
                    }

                    BlockEntity blockEntity = level.getBlockEntity(shipPos);

                    CompoundTag nbtData = null;
                    if (blockEntity != null) {
                        nbtData = blockEntity.saveWithFullMetadata();
                        if (blockEntity instanceof BaseContainerBlockEntity containerBlockEntity) {
                            for (int slot = 0; slot < containerBlockEntity.getContainerSize(); slot++) {
                                containerBlockEntity.setItem(slot, new ItemStack(Items.AIR));
                            }
                        }
                    }

                    level.setBlock(shipPos, Blocks.AIR.defaultBlockState(), 3 | 16);
                    level.setBlock(worldPos, shipState, 3 | 16);

                    if (nbtData != null) {
                        BlockEntity newBlockEntity = level.getBlockEntity(worldPos);
                        if (newBlockEntity != null) {
                            newBlockEntity.load(nbtData);
                        }
                    }


                    newBlockPositions.add(worldPos);
                }
            }
        }

        int minx = Integer.MAX_VALUE;
        int miny = Integer.MAX_VALUE;
        int minz = Integer.MAX_VALUE;

        for (BlockPos newBlockPosition : newBlockPositions) {
            if (newBlockPosition.getX() < minx) {
                minx = newBlockPosition.getX();
            }
            if (newBlockPosition.getY() < miny) {
                miny = newBlockPosition.getY();
            }
            if (newBlockPosition.getZ() < minz) {
                minz = newBlockPosition.getZ();
            }
        }

        double playerOffsetX = worldAABB.minX() - minx;
        double playerOffsetY = worldAABB.minY() - 0.5 - miny;
        double playerOffsetZ = worldAABB.minZ() - minz;

        for (ServerPlayer player : this.level.getServer().getPlayerList().getPlayers()) {
            Vec3 playerPos = player.position();

            if (worldAABB.containsPoint(new Vector3d(playerPos.x(), playerPos.y(), playerPos.z()))) {
                player.teleportTo(playerPos.x() - playerOffsetX, playerPos.y() - playerOffsetY + 0.1, playerPos.z() - playerOffsetZ);
            }
        }

        return newBlockPositions;
    }

    @Override
    public void onChunkUnloaded() {
        CoreUtil.coreEntities.remove(this);
        super.onChunkUnloaded();
    }

    public boolean hasPassenger() {
        MinecraftServer server = this.level.getServer();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Vec3 playerPos = player.position();
            Vector3dc playerPosVS = new Vector3d(playerPos.x(), playerPos.y(), playerPos.z());

            Vector3d shipOffset = new Vector3d(0, 0, 0);
            this.serverShip.getTransform().getPositionInWorld().sub(playerPosVS, shipOffset);

            if (this.serverShip.getWorldAABB().containsPoint(playerPosVS)) {
                return true;
            }
        }

        return false;
    }

    private void givePassengersGravity() {
        MinecraftServer server = this.level.getServer();
        AABBdc shipAABB = this.serverShip.getWorldAABB();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Vec3 playerPos = player.position();
            Vector3dc playerPosVS = new Vector3d(playerPos.x(), playerPos.y(), playerPos.z());

            if (shipAABB.containsPoint(playerPosVS) && this.level.dimension().location().getPath().equals(player.level().dimension().location().getPath())) {
                CoreUtil.hangingPlayers.remove(player);
                player.setNoGravity(false);
            }
        }
    }

    private void clearAreaNearShip() {
        ServerLevel serverLevel = (ServerLevel) this.level;
        AABBdc shipAABB = this.serverShip.getWorldAABB();
        AABB areaToClear = new AABB(new BlockPos((int) shipAABB.minX() - 10, (int) shipAABB.minY() - 4, (int) shipAABB.minZ() - 10), new BlockPos((int) shipAABB.minX() + 10, (int) shipAABB.minY() + 4, (int) shipAABB.minZ() + 10));

        for (double x = areaToClear.minX; x <= areaToClear.maxX; x++) {
            for (double y = areaToClear.minY; y <= areaToClear.maxY; y++) {
                for (double z = areaToClear.minZ; z <= areaToClear.maxZ; z++) {
                    serverLevel.removeBlock(new BlockPos((int) x, (int) y, (int) z), false);
                }
            }
        }
    }

    private void updateShipReference(ServerLevel serverLevel, BlockPos pos) {
        this.serverShip = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, pos.getX(), pos.getY(), pos.getZ());
        if (this.serverShip != null) {
            this.forceApplier = this.serverShip.getAttachment(ModForceApplier.class);
        }
        if (this.forceApplier == null) {
            if (this.serverShip != null) {
                this.serverShip.saveAttachment(ModForceApplier.class, new ModForceApplier());
            }
        }

        if (this.serverShip == null) {
            assemble(serverLevel);
        }
    }
}
