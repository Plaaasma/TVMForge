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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.block.ModBlockEntities;
import net.nerdorg.vortexmod.contraption.ModForceApplier;
import net.nerdorg.vortexmod.contraption.Stabilize;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3i;
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
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.physics_api.PhysicsBodyReference;

import java.util.ArrayList;
import java.util.List;

public class TardisCoreBlockEntity extends BlockEntity {
    public static List<TardisCoreBlockEntity> coreEntities = new ArrayList<>();

    private BlockPos targetPos = new BlockPos(0, 200, 0);
    private ServerShip serverShip;
    private ModForceApplier forceApplier;
    private boolean moveToTarget;

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

        if (this.serverShip == null || this.forceApplier == null) {
            updateShipReference((ServerLevel) pLevel, pPos);
        }
        else {
            if (this.moveToTarget) {
                this.moveToTarget = false;
                forceTowardsTarget(this.forceApplier);
            }
        }

        if (!coreEntities.contains(this)) {
            coreEntities.add(this);
        }

        setChanged(pLevel, pPos, pState);
    }

    @Override
    public void onChunkUnloaded() {
        coreEntities.remove(this);
        super.onChunkUnloaded();
    }

    public void teleportWithDimChange(ServerLevel targetLevel, double x, double y, double z) {
        ServerLevel currentLevel = (ServerLevel) this.getLevel();
        MinecraftServer server = currentLevel.getServer();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Vec3 playerPos = player.position();
            Vector3dc playerPosVS = new Vector3d(playerPos.x(), playerPos.y(), playerPos.z());

            Vector3d shipOffset = new Vector3d(0, 0, 0);
            this.serverShip.getTransform().getPositionInWorld().sub(playerPosVS, shipOffset);

            if (this.serverShip.getWorldAABB().containsPoint(playerPosVS)) {
                player.teleportTo(targetLevel,
                        x - shipOffset.x(), y - shipOffset.y() + 1, z - shipOffset.z(),
                        RelativeMovement.ALL,
                        player.getYHeadRot(),
                        player.getXRot());
            }
        }

        ShipTeleportDataImpl shipTeleportData = new ShipTeleportDataImpl(
                new Vector3d(x, y, z),
                this.serverShip.getTransform().getShipToWorldRotation(),
                this.serverShip.getVelocity(),
                this.serverShip.getOmega(),
                VSGameUtilsKt.getDimensionId(targetLevel),
                1.0
        );
        VSGameUtilsKt.getVsCore().teleportShip(VSGameUtilsKt.getShipObjectWorld(currentLevel), this.serverShip, shipTeleportData);
    }

    public void forceTowardsTarget(ModForceApplier forceApplier) {
        doVerticleForceToTarget(forceApplier);
        doHorizontalForceToTarget(forceApplier);
    }

    public void doVerticleForceToTarget(ModForceApplier forceApplier) {
        Vector3d endVelocity = new Vector3d(0, 0, 0);

        double currentY = this.serverShip.getTransform().getPositionInWorld().y();
        double mass = this.serverShip.getInertiaData().getMass();
        double gravity = 9.8;  // Approximation of gravitational acceleration in m/s^2 (adjust as necessary)

        // Calculate the deviation from the target position
        double deviation = currentY - this.targetPos.getY();

        // Proportional control strength (tuning parameter)
        double controlStrength = 30;

        // Getting the current vertical velocity
        double currentVelocityY = this.serverShip.getVelocity().y();

        // Maximum allowable velocity
        double maxVelocity = mass * 31;

        // Calculate the minimum necessary force to "float"
        double floatingForce = mass * gravity;

        // Calculate corrective force based on deviation and damping based on excess velocity
        double correctiveForce = -deviation * mass * controlStrength - currentVelocityY * mass * 10;

        // Check if the current velocity is already too high
        if (currentVelocityY > maxVelocity) {
            correctiveForce = 0;  // No additional upward force if maximum velocity is exceeded
        } else if (currentVelocityY + correctiveForce/mass > maxVelocity) {
            // Cap the corrective force to not exceed the maximum velocity
            correctiveForce = (maxVelocity - currentVelocityY) * mass;
        }

        // Total force to apply
        double totalForce = floatingForce + correctiveForce;

        if (totalForce < -35 * mass) {
            totalForce = -35 * mass;
        }
        if (totalForce > 35 * mass) {
            totalForce = 35 * mass;
        }

        // Applying the total force to achieve floating effect and stabilize at targetY
        endVelocity = endVelocity.add(0, totalForce, 0);

        // Applying the torque as an invariant force
        forceApplier.applyInvariantForce((Vector3dc) endVelocity);
    }

    public void doHorizontalForceToTarget(ModForceApplier forceApplier) {
        Vector3d endVelocity = new Vector3d(0, 0, 0);

        // Retrieve the current position and mass of the ship
        Vector3dc currentPosition = this.serverShip.getTransform().getPositionInWorld();
        double mass = this.serverShip.getInertiaData().getMass();

        // Get the horizontal components of the target position and the current position
        double targetX = this.targetPos.getX();
        double targetZ = this.targetPos.getZ();
        double currentX = currentPosition.x();
        double currentZ = currentPosition.z();

        // Calculate the deviations from the target position
        double deviationX = targetX - currentX;
        double deviationZ = targetZ - currentZ;

        // Proportional control strength (tuning parameter)
        double controlStrength = 30;

        // Current velocities in the X and Z directions
        double currentVelocityX = this.serverShip.getVelocity().x();
        double currentVelocityZ = this.serverShip.getVelocity().z();

        // Maximum allowable velocity
        double maxVelocity = 30;  // Define the max horizontal velocity

        // Calculate corrective forces based on deviations and damping based on excess velocities
        double correctiveForceX = deviationX * mass * controlStrength - currentVelocityX * mass * 10;
        double correctiveForceZ = deviationZ * mass * controlStrength - currentVelocityZ * mass * 10;

        // Cap corrective forces to not exceed maximum velocities
        if (Math.abs(currentVelocityX + correctiveForceX/mass) > maxVelocity) {
            correctiveForceX = (Math.signum(correctiveForceX) * maxVelocity - currentVelocityX) * mass;
        }
        if (Math.abs(currentVelocityZ + correctiveForceZ/mass) > maxVelocity) {
            correctiveForceZ = (Math.signum(correctiveForceZ) * maxVelocity - currentVelocityZ) * mass;
        }

        // Total forces to apply
        double totalForceX = correctiveForceX;
        double totalForceZ = correctiveForceZ;

        // Applying the total forces to achieve horizontal movement
        endVelocity = endVelocity.add(totalForceX, 0, totalForceZ);

        // Applying the torque as an invariant force (no changes in Y-direction force)
        forceApplier.applyInvariantForce((Vector3dc) endVelocity);
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
    }
}
