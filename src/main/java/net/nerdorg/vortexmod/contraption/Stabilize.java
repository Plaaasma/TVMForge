package net.nerdorg.vortexmod.contraption;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

@Metadata(
        mv = {1, 9, 0},
        k = 2,
        xi = 48,
        d1 = {"\u0000,\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0001H\u0002\u001a\u0018\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0001H\u0002\u001a6\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010¨\u0006\u0012"},
        d2 = {"smoothingATan", "", "smoothing", "x", "smoothingATanMax", "max", "stabilize", "", "ship", "Lorg/valkyrienskies/core/impl/game/ships/PhysShipImpl;", "omega", "Lorg/joml/Vector3dc;", "vel", "forces", "Lorg/valkyrienskies/core/api/ships/PhysShip;", "linear", "", "yaw", "vortexmod"}
)
public final class Stabilize {
    public static final void stabilize(@NotNull PhysShipImpl ship, @NotNull Vector3dc omega, @NotNull Vector3dc vel, @NotNull PhysShip forces, boolean linear, boolean yaw) {
        Intrinsics.checkNotNullParameter(ship, "ship");
        Intrinsics.checkNotNullParameter(omega, "omega");
        Intrinsics.checkNotNullParameter(vel, "vel");
        Intrinsics.checkNotNullParameter(forces, "forces");
        Vector3d shipUp = new Vector3d(0.0, 1.0, 0.0);
        Vector3d worldUp = new Vector3d(0.0, 1.0, 0.0);
        ship.getPoseVel().getRot().transform(shipUp);
        double angleBetween = shipUp.angle((Vector3dc)worldUp);
        Vector3d idealAngularAcceleration = new Vector3d();
        Vector3d stabilizationTorque;
        if (angleBetween > 0.01) {
            stabilizationTorque = shipUp.cross((Vector3dc)worldUp, new Vector3d()).normalize();
            idealAngularAcceleration.add((Vector3dc)stabilizationTorque.mul(angleBetween, stabilizationTorque));
        }

        idealAngularAcceleration.sub(omega.x(), !yaw ? 0.0 : omega.y(), omega.z());
        stabilizationTorque = ship.getPoseVel().getRot().transform(ship.getInertia().getMomentOfInertiaTensor().transform(ship.getPoseVel().getRot().transformInverse(idealAngularAcceleration)));
        stabilizationTorque.mul(15.0);
        Intrinsics.checkNotNull(stabilizationTorque);
        forces.applyInvariantTorque((Vector3dc)stabilizationTorque);
        if (linear) {
            Vector3d idealVelocity = (new Vector3d(vel)).negate();
            idealVelocity.y = 0.0;
            double s = (double)1 * ((double)1 - (double)1 / smoothingATanMax(10000.0, ship.getInertia().getShipMass() * 2.0E-4 + 1.0)) / 10.0;
            if (idealVelocity.lengthSquared() > s * s) {
                idealVelocity.normalize(s);
            }

            idealVelocity.mul(ship.getInertia().getShipMass() * 9.2);
            Intrinsics.checkNotNull(idealVelocity);
            forces.applyInvariantForce((Vector3dc)idealVelocity);
        }

    }

    private static final double smoothingATan(double smoothing, double x) {
        return Math.atan(x * smoothing) / smoothing;
    }

    private static final double smoothingATanMax(double max, double x) {
        return smoothingATan((double)1 / (max * 0.638), x);
    }
}
