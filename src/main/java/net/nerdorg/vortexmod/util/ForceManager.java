package net.nerdorg.vortexmod.util;

import net.nerdorg.vortexmod.block.entity.TardisCoreBlockEntity;
import net.nerdorg.vortexmod.contraption.ModForceApplier;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ForceManager {
    public static void forceTowardsTarget(ModForceApplier forceApplier, TardisCoreBlockEntity tardisCoreBlockEntity) {
        doVerticleForceToTarget(forceApplier, tardisCoreBlockEntity);
        doHorizontalForceToTarget(forceApplier, tardisCoreBlockEntity);
    }

    public static void forceTowardsTarget(ModForceApplier forceApplier, TardisCoreBlockEntity tardisCoreBlockEntity, double x, double y, double z) {
        doVerticleForceToTarget(forceApplier, tardisCoreBlockEntity, x, y, z);
        if (tardisCoreBlockEntity.hasPassenger()) {
            doHorizontalForceToTarget(forceApplier, tardisCoreBlockEntity, x, y, z);
        }
    }

    public static void doVerticleForceToTarget(ModForceApplier forceApplier, TardisCoreBlockEntity tardisCoreBlockEntity) {
        Vector3d endVelocity = new Vector3d(0, 0, 0);

        double currentY = tardisCoreBlockEntity.getServerShip().getTransform().getPositionInWorld().y();
        double mass = tardisCoreBlockEntity.getServerShip().getInertiaData().getMass();
        double gravity = 9.8;  // Approximation of gravitational acceleration in m/s^2 (adjust as necessary)

        // Calculate the deviation from the target position
        double deviation = currentY - tardisCoreBlockEntity.getTargetPos().getY();

        // Proportional control strength (tuning parameter)
        double controlStrength = 30;

        // Getting the current vertical velocity
        double currentVelocityY = tardisCoreBlockEntity.getServerShip().getVelocity().y();

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

    public static void doVerticleForceToTarget(ModForceApplier forceApplier, TardisCoreBlockEntity tardisCoreBlockEntity, double x, double y, double z) {
        Vector3d endVelocity = new Vector3d(0, 0, 0);

        double currentY = tardisCoreBlockEntity.getServerShip().getTransform().getPositionInWorld().y();
        double mass = tardisCoreBlockEntity.getServerShip().getInertiaData().getMass();
        double gravity = 9.8;  // Approximation of gravitational acceleration in m/s^2 (adjust as necessary)

        // Calculate the deviation from the target position
        double deviation = currentY - y;

        // Proportional control strength (tuning parameter)
        double controlStrength = 30;

        // Getting the current vertical velocity
        double currentVelocityY = tardisCoreBlockEntity.getServerShip().getVelocity().y();

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

    public static void doHorizontalForceToTarget(ModForceApplier forceApplier, TardisCoreBlockEntity tardisCoreBlockEntity) {
        Vector3d endVelocity = new Vector3d(0, 0, 0);

        // Retrieve the current position and mass of the ship
        Vector3dc currentPosition = tardisCoreBlockEntity.getServerShip().getTransform().getPositionInWorld();
        double mass = tardisCoreBlockEntity.getServerShip().getInertiaData().getMass();

        // Get the horizontal components of the target position and the current position
        double targetX = tardisCoreBlockEntity.getTargetPos().getX();
        double targetZ = tardisCoreBlockEntity.getTargetPos().getZ();
        double currentX = currentPosition.x();
        double currentZ = currentPosition.z();

        // Calculate the deviations from the target position
        double deviationX = targetX - currentX;
        double deviationZ = targetZ - currentZ;

        // Proportional control strength (tuning parameter)
        double controlStrength = 30;

        // Current velocities in the X and Z directions
        double currentVelocityX = tardisCoreBlockEntity.getServerShip().getVelocity().x();
        double currentVelocityZ = tardisCoreBlockEntity.getServerShip().getVelocity().z();

        // Maximum allowable velocity
        double maxVelocity = 20;  // Define the max horizontal velocity

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

    public static void doHorizontalForceToTarget(ModForceApplier forceApplier, TardisCoreBlockEntity tardisCoreBlockEntity, double x, double y, double z) {
        Vector3d endVelocity = new Vector3d(0, 0, 0);

        // Retrieve the current position and mass of the ship
        Vector3dc currentPosition = tardisCoreBlockEntity.getServerShip().getTransform().getPositionInWorld();
        double mass = tardisCoreBlockEntity.getServerShip().getInertiaData().getMass();

        // Get the horizontal components of the target position and the current position
        double currentX = currentPosition.x();
        double currentZ = currentPosition.z();

        // Calculate the deviations from the target position
        double deviationX = x - currentX;
        double deviationZ = z - currentZ;

        // Proportional control strength (tuning parameter)
        double controlStrength = 20;

        // Current velocities in the X and Z directions
        double currentVelocityX = tardisCoreBlockEntity.getServerShip().getVelocity().x();
        double currentVelocityZ = tardisCoreBlockEntity.getServerShip().getVelocity().z();

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
}
