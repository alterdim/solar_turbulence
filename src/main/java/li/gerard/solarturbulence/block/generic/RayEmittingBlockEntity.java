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

import java.util.HashSet;
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

        // Clear reflectors activated in the previous tick
        for (BlockPos rp : be.activatedReflectors) {
            BlockEntity rbe = level.getBlockEntity(rp);
            if (rbe instanceof IRayReflector r) r.clearRayOutput();
        }
        be.activatedReflectors.clear();

        int[] seg = new int[2]; // [0]=beamLength, [1]=pointerLength
        traceRay(level, be, pos, be.getEmitDirection(), be.getMaxBounces(), be.getEmissionMultiplier(), seg);

        if (seg[0] != be.beamLength || seg[1] != be.pointerLength) {
            be.beamLength = seg[0];
            be.pointerLength = seg[1];
            level.sendBlockUpdated(pos, state, state, 3);
            be.setChanged();
        }
    }

    /**
     * Traces one ray segment from {@code origin} in {@code direction}, following reflectors.
     * Fills {@code seg[0]} with beam length and {@code seg[1]} with pointer length for THIS segment.
     * Returns whether a heat target was reached anywhere in the chain from this segment onward.
     */
    private static boolean traceRay(Level level, RayEmittingBlockEntity rootBe,
                                     BlockPos origin, Direction direction,
                                     int remainingBounces, float heatMultiplier, int[] seg) {
        int range = rootBe.getMaxRange();

        for (int dist = 1; dist <= range; dist++) {
            BlockPos scanPos = origin.relative(direction, dist);
            BlockState scanState = level.getBlockState(scanPos);

            // Check for a reflector before other target/solid checks
            if (remainingBounces > 0) {
                BlockEntity candidate = level.getBlockEntity(scanPos);
                if (candidate instanceof IRayReflector reflector && reflector.canReflect()) {
                    rootBe.activatedReflectors.add(scanPos);

                    Direction outDir = reflector.reflect(direction);
                    float outMultiplier = heatMultiplier * reflector.getHeatMultiplier();
                    int[] reflectedSeg = new int[2];
                    boolean chainHit = traceRay(level, rootBe, scanPos, outDir,
                            remainingBounces - 1, outMultiplier, reflectedSeg);

                    reflector.updateRayOutput(outDir, reflectedSeg[0], reflectedSeg[1]);

                    if (chainHit) {
                        seg[0] = dist;
                        seg[1] = 0;
                    } else {
                        seg[0] = 0;
                        seg[1] = dist;
                    }
                    return chainHit;
                }
            }

            // Heat target
            if (rootBe.isValidTarget(level, scanPos, direction)) {
                boolean hit = rootBe.onHitTarget(level, scanPos, direction, heatMultiplier);
                seg[0] = hit ? dist : 0;
                seg[1] = hit ? 0 : dist;
                return hit;
            }

            // Solid non-target — stop here
            if (!scanState.isAir()) {
                seg[0] = 0;
                seg[1] = dist;
                return false;
            }
        }

        // Exhausted range
        seg[0] = 0;
        seg[1] = range;
        return false;
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
