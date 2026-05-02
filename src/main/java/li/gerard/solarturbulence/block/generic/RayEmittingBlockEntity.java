package li.gerard.solarturbulence.block.generic;

import com.lowdragmc.lowdraglib2.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib2.syncdata.holder.blockentity.ISyncPersistRPCBlockEntity;
import com.lowdragmc.lowdraglib2.syncdata.storage.FieldManagedStorage;
import com.lowdragmc.lowdraglib2.syncdata.storage.IManagedStorage;
import li.gerard.solarturbulence.capability.ModCapabilities;
import li.gerard.solarturbulence.capability.heat.HeatStorage;
import li.gerard.solarturbulence.capability.heat.IHeatStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class RayEmittingBlockEntity extends BlockEntity implements ISyncPersistRPCBlockEntity {

    private final FieldManagedStorage syncStorage = new FieldManagedStorage(this);

    @Persisted
    @DescSynced
    protected final HeatStorage heatStorage;

    @DescSynced
    private int extractSpeed;

    @DescSynced
    private int beamLength = 0;

    @DescSynced
    private int pointerLength = 0;

    /** Server-side only: reflectors activated during the last tick, used to clear stale outputs. */
    private final Set<BlockPos> activatedReflectors = new HashSet<>();

    /**
     * Fractional heat remainder from the previous tick's multiplier application.
     * Carries the sub-integer portion forward so the average delivery matches
     * exactly extractSpeed * cumulativeMultiplier over time, regardless of rounding.
     * Not persisted — loss of at most ~1 heat unit on reload is negligible.
     */
    private float reflectionHeatRemainder = 0f;

    protected RayEmittingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState,
                                      int heatCapacity, int heatMaxReceive, int heatMaxExtract) {
        super(type, pos, blockState);
        heatStorage = new HeatStorage(heatCapacity, heatMaxReceive, heatMaxExtract);
        extractSpeed = heatMaxExtract;
    }

    /** Direction the primary ray fires in. */
    public abstract Direction getEmitDirection();

    /** Maximum range in blocks for each ray segment (including reflected segments). */
    protected abstract int getMaxRange();

    /** Maximum number of mirror bounces before the ray stops reflecting. */
    protected int getMaxBounces() { return 4; }

    /**
     * Starting heat multiplier for the ray emitted by this block (0–1).
     * Default: 1.0 (full efficiency). Override in subclasses to model emitter-side losses.
     */
    protected float getEmissionMultiplier() { return 1.0f; }

    protected boolean isValidTarget(Level level, BlockPos pos, Direction incomingDir) {
        IHeatStorage target = level.getCapability(ModCapabilities.HEAT, pos, incomingDir.getOpposite());
        return target != null && target.canReceiveHeat();
    }

    protected boolean onHitTarget(Level level, BlockPos targetPos, Direction incomingDir, float heatMultiplier) {
        if (heatStorage.getHeatStored() <= 0) return false;
        IHeatStorage target = level.getCapability(ModCapabilities.HEAT, targetPos, incomingDir.getOpposite());
        if (target == null) return false;

        // Apply multiplier with fractional accumulator so the average delivery equals
        // exactly extractSpeed * heatMultiplier per tick, independent of rounding.
        int toGive;
        if (heatMultiplier >= 1.0f) {
            reflectionHeatRemainder = 0f;
            toGive = heatStorage.extractHeat(extractSpeed, true);
        } else {
            float effective = extractSpeed * heatMultiplier + reflectionHeatRemainder;
            toGive = (int) effective;
            reflectionHeatRemainder = effective - toGive;
            toGive = Math.min(toGive, heatStorage.extractHeat(extractSpeed, true));
        }

        int accepted = target.receiveHeat(toGive, true);
        if (accepted > 0) {
            heatStorage.extractHeat(accepted, false);
            target.receiveHeat(accepted, false);
            return true;
        }
        return false;
    }

    protected void preTick(Level level, BlockPos pos, BlockState state) {}

    // -------------------------------------------------------------------------
    // Tick
    // -------------------------------------------------------------------------

    public static void tick(Level level, BlockPos pos, BlockState state, RayEmittingBlockEntity be) {
        if (level.isClientSide) return;
        be.preTick(level, pos, state);

        // Snapshot previous reflectors so we can clear any that fall out of the chain this tick.
        Set<BlockPos> prevReflectors = be.activatedReflectors.isEmpty()
                ? Collections.emptySet()
                : new HashSet<>(be.activatedReflectors);
        be.activatedReflectors.clear();

        RaySegment seg = traceRay(level, be, pos, be.getEmitDirection(), be.getMaxBounces(), be.getEmissionMultiplier());

        // Only clear reflectors that are no longer in the chain — avoids a clear+restore every tick.
        for (BlockPos rp : prevReflectors) {
            if (!be.activatedReflectors.contains(rp)) {
                BlockEntity rbe = level.getBlockEntity(rp);
                if (rbe instanceof IRayReflector r) r.clearRayOutput();
            }
        }

        if (seg.beamLength() != be.beamLength || seg.pointerLength() != be.pointerLength) {
            be.beamLength = seg.beamLength();
            be.pointerLength = seg.pointerLength();
            level.sendBlockUpdated(pos, state, state, 3);
            be.setChanged();
        }
    }

    // -------------------------------------------------------------------------
    // Ray tracing
    // -------------------------------------------------------------------------

    private record RaySegment(int beamLength, int pointerLength, float power) {}

    /**
     * Traces one ray segment from {@code origin} in {@code direction}, following reflectors.
     * Returns a {@link RaySegment} with beam/pointer lengths for THIS segment and the power at the hit point.
     */
    private static RaySegment traceRay(Level level, RayEmittingBlockEntity rootBe,
                                        BlockPos origin, Direction direction,
                                        int remainingBounces, float heatMultiplier) {
        int range = rootBe.getMaxRange();

        for (int dist = 1; dist <= range; dist++) {
            BlockPos scanPos = origin.relative(direction, dist);
            BlockState scanState = level.getBlockState(scanPos);

            // Check for a reflector before other target/solid checks.
            if (remainingBounces > 0) {
                BlockEntity candidate = level.getBlockEntity(scanPos);
                if (candidate instanceof IRayReflector reflector && reflector.canReflect()) {
                    rootBe.activatedReflectors.add(scanPos);

                    List<IRayReflector.RayOutput> outputs = reflector.getOutputs(direction);
                    boolean anyChainHit = false;
                    for (IRayReflector.RayOutput out : outputs) {
                        float outMultiplier = heatMultiplier * out.powerFactor();
                        RaySegment sub = traceRay(level, rootBe, scanPos, out.dir(),
                                remainingBounces - 1, outMultiplier);
                        reflector.updateRayOutput(out.dir(), sub.beamLength(), sub.pointerLength(), sub.power());
                        if (sub.beamLength() > 0) anyChainHit = true;
                    }
                    return anyChainHit
                            ? new RaySegment(dist, 0, heatMultiplier)
                            : new RaySegment(0, dist, 0f);
                }
            }

            // Heat target.
            if (rootBe.isValidTarget(level, scanPos, direction)) {
                boolean hit = rootBe.onHitTarget(level, scanPos, direction, heatMultiplier);
                return hit ? new RaySegment(dist, 0, heatMultiplier) : new RaySegment(0, dist, 0f);
            }

            // Solid non-target — stop here.
            if (!scanState.isAir()) {
                return new RaySegment(0, dist, 0f);
            }
        }

        return new RaySegment(0, range, 0f);
    }

    // -------------------------------------------------------------------------
    // Cleanup on removal
    // -------------------------------------------------------------------------

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide) {
            for (BlockPos rp : activatedReflectors) {
                BlockEntity rbe = level.getBlockEntity(rp);
                if (rbe instanceof IRayReflector r) r.clearRayOutput();
            }
            activatedReflectors.clear();
        }
        super.setRemoved();
    }

    // -------------------------------------------------------------------------
    // Renderer helpers
    // -------------------------------------------------------------------------

    public int getBeamLength() { return beamLength; }

    public int getPointerLength() { return pointerLength; }

    @Override
    public IManagedStorage getSyncStorage() {
        return syncStorage;
    }
}
