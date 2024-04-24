package net.nerdorg.vortexmod.contraption;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Metadata(
        mv = {1, 9, 0},
        k = 1,
        xi = 48,
        d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\u0018\u00002\u00020\u0001:\u0001\"B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J\u000e\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u0005J\u0016\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u0005J\u000e\u0010\u001c\u001a\u00020\u00152\u0006\u0010\u001d\u001a\u00020\u0005J\u000e\u0010\u001e\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u0005J\u000e\u0010\u001f\u001a\u00020\u00152\u0006\u0010\u001d\u001a\u00020\u0005J\u000e\u0010 \u001a\u00020\u00152\u0006\u0010!\u001a\u00020\fR\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u000e\"\u0004\b\u0013\u0010\u0010¨\u0006#"},
        d2 = {"Lorg/valkyrienskies/mod/common/util/GameTickForceApplier;", "Lorg/valkyrienskies/core/api/ships/ShipForcesInducer;", "()V", "invForces", "Ljava/util/concurrent/ConcurrentLinkedQueue;", "Lorg/joml/Vector3dc;", "invPosForces", "Lorg/valkyrienskies/mod/common/util/GameTickForceApplier$InvForceAtPos;", "invTorques", "rotForces", "rotTorques", "toBeStatic", "", "getToBeStatic", "()Z", "setToBeStatic", "(Z)V", "toBeStaticUpdated", "getToBeStaticUpdated", "setToBeStaticUpdated", "applyForces", "", "physShip", "Lorg/valkyrienskies/core/api/ships/PhysShip;", "applyInvariantForce", "force", "applyInvariantForceToPos", "pos", "applyInvariantTorque", "torque", "applyRotDependentForce", "applyRotDependentTorque", "setStatic", "b", "InvForceAtPos", "sources for library Gradle: org.valkyrienskies:valkyrienskies-120-forge:2.1.3-beta.1+a52f38bd68_mapped_parchment_2023.10.08-1.20.2"}
)
public final class ModForceApplier implements ShipForcesInducer {
    @NotNull
    private final ConcurrentLinkedQueue invForces = new ConcurrentLinkedQueue();
    @NotNull
    private final ConcurrentLinkedQueue invTorques = new ConcurrentLinkedQueue();
    @NotNull
    private final ConcurrentLinkedQueue rotForces = new ConcurrentLinkedQueue();
    @NotNull
    private final ConcurrentLinkedQueue rotTorques = new ConcurrentLinkedQueue();
    @NotNull
    private final ConcurrentLinkedQueue invPosForces = new ConcurrentLinkedQueue();
    private volatile boolean toBeStatic;
    private volatile boolean toBeStaticUpdated;

    public final boolean getToBeStatic() {
        return this.toBeStatic;
    }

    public final void setToBeStatic(boolean var1) {
        this.toBeStatic = var1;
    }

    public final boolean getToBeStaticUpdated() {
        return this.toBeStaticUpdated;
    }

    public final void setToBeStaticUpdated(boolean var1) {
        this.toBeStaticUpdated = var1;
    }

    public void applyForces(@NotNull PhysShip physShip) {
        Intrinsics.checkNotNullParameter(physShip, "physShip");

        PhysShipImpl physShipImpl = (PhysShipImpl) physShip;

        Vector3dc omega = physShipImpl.getPoseVel().getOmega();
        Vector3dc vel = physShipImpl.getPoseVel().getVel();

        Stabilize.stabilize(
                physShipImpl,
                omega,
                vel,
                physShipImpl,
                false,
                true
        );

        Queue $this$pollUntilEmpty$iv = (Queue)this.invForces;

        while(true) {
            Object var10000 = $this$pollUntilEmpty$iv.poll();
            Vector3dc p0;
            if (var10000 == null) {
                $this$pollUntilEmpty$iv = (Queue)this.invTorques;

                while(true) {
                    var10000 = $this$pollUntilEmpty$iv.poll();
                    if (var10000 == null) {
                        $this$pollUntilEmpty$iv = (Queue)this.rotForces;

                        while(true) {
                            var10000 = $this$pollUntilEmpty$iv.poll();
                            if (var10000 == null) {
                                $this$pollUntilEmpty$iv = (Queue)this.rotTorques;

                                while(true) {
                                    var10000 = $this$pollUntilEmpty$iv.poll();
                                    if (var10000 == null) {
                                        $this$pollUntilEmpty$iv = (Queue)this.invPosForces;

                                        while(true) {
                                            var10000 = $this$pollUntilEmpty$iv.poll();
                                            if (var10000 == null) {
                                                if (this.toBeStaticUpdated) {
                                                    physShip.setStatic(this.toBeStatic);
                                                    this.toBeStaticUpdated = false;
                                                }

                                                return;
                                            }

                                            InvForceAtPos var8 = (InvForceAtPos)var10000;
                                            Vector3dc force = var8.component1();
                                            Vector3dc pos = var8.component2();
                                            physShip.applyInvariantForceToPos(force, pos);
                                        }
                                    }

                                    p0 = (Vector3dc)var10000;
                                    physShip.applyRotDependentTorque(p0);
                                }
                            }

                            p0 = (Vector3dc)var10000;
                            physShip.applyRotDependentForce(p0);
                        }
                    }

                    p0 = (Vector3dc)var10000;
                    physShip.applyInvariantTorque(p0);
                }
            }

            p0 = (Vector3dc)var10000;
            physShip.applyInvariantForce(p0);
        }
    }

    public final void applyInvariantForce(@NotNull Vector3dc force) {
        Intrinsics.checkNotNullParameter(force, "force");
        this.invForces.add(force);
    }

    public final void applyInvariantTorque(@NotNull Vector3dc torque) {
        Intrinsics.checkNotNullParameter(torque, "torque");
        this.invForces.add(torque);
    }

    public final void applyRotDependentForce(@NotNull Vector3dc force) {
        Intrinsics.checkNotNullParameter(force, "force");
        this.invForces.add(force);
    }

    public final void applyRotDependentTorque(@NotNull Vector3dc torque) {
        Intrinsics.checkNotNullParameter(torque, "torque");
        this.invForces.add(torque);
    }

    public final void applyInvariantForceToPos(@NotNull Vector3dc force, @NotNull Vector3dc pos) {
        Intrinsics.checkNotNullParameter(force, "force");
        Intrinsics.checkNotNullParameter(pos, "pos");
        this.invPosForces.add(new InvForceAtPos(force, pos));
    }

    public final void setStatic(boolean b) {
        this.toBeStatic = b;
        this.toBeStaticUpdated = true;
    }

    @Metadata(
            mv = {1, 9, 0},
            k = 1,
            xi = 48,
            d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003HÆ\u0003J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u000f\u001a\u00020\u0010HÖ\u0001J\t\u0010\u0011\u001a\u00020\u0012HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007¨\u0006\u0013"},
            d2 = {"Lorg/valkyrienskies/mod/common/util/GameTickForceApplier$InvForceAtPos;", "", "force", "Lorg/joml/Vector3dc;", "pos", "(Lorg/joml/Vector3dc;Lorg/joml/Vector3dc;)V", "getForce", "()Lorg/joml/Vector3dc;", "getPos", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "sources for library Gradle: org.valkyrienskies:valkyrienskies-120-forge:2.1.3-beta.1+a52f38bd68_mapped_parchment_2023.10.08-1.20.2"}
    )
    private static final class InvForceAtPos {
        @NotNull
        private final Vector3dc force;
        @NotNull
        private final Vector3dc pos;

        public InvForceAtPos(@NotNull Vector3dc force, @NotNull Vector3dc pos) {
            super();
            Intrinsics.checkNotNullParameter(force, "force");
            Intrinsics.checkNotNullParameter(pos, "pos");
            this.force = force;
            this.pos = pos;
        }

        @NotNull
        public final Vector3dc getForce() {
            return this.force;
        }

        @NotNull
        public final Vector3dc getPos() {
            return this.pos;
        }

        @NotNull
        public final Vector3dc component1() {
            return this.force;
        }

        @NotNull
        public final Vector3dc component2() {
            return this.pos;
        }

        @NotNull
        public final InvForceAtPos copy(@NotNull Vector3dc force, @NotNull Vector3dc pos) {
            Intrinsics.checkNotNullParameter(force, "force");
            Intrinsics.checkNotNullParameter(pos, "pos");
            return new InvForceAtPos(force, pos);
        }

        // $FF: synthetic method
        public static InvForceAtPos copy$default(InvForceAtPos var0, Vector3dc var1, Vector3dc var2, int var3, Object var4) {
            if ((var3 & 1) != 0) {
                var1 = var0.force;
            }

            if ((var3 & 2) != 0) {
                var2 = var0.pos;
            }

            return var0.copy(var1, var2);
        }

        @NotNull
        public String toString() {
            return "InvForceAtPos(force=" + this.force + ", pos=" + this.pos + ')';
        }

        public int hashCode() {
            int result = this.force.hashCode();
            result = result * 31 + this.pos.hashCode();
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            } else if (!(other instanceof InvForceAtPos)) {
                return false;
            } else {
                InvForceAtPos var2 = (InvForceAtPos)other;
                if (!Intrinsics.areEqual(this.force, var2.force)) {
                    return false;
                } else {
                    return Intrinsics.areEqual(this.pos, var2.pos);
                }
            }
        }
    }
}
