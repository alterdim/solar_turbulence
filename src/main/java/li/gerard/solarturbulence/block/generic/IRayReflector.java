package li.gerard.solarturbulence.block.generic;

import net.minecraft.core.Direction;

import java.util.List;

/**
 * Implemented by any block entity that can intercept and redirect a heat ray.
 * The ray system queries this interface; concrete mirror/splitter behaviour lives in the implementation.
 */
public interface IRayReflector {

    /** A single outgoing ray from a reflector, with its power factor relative to the incoming ray. */
    record RayOutput(Direction dir, float powerFactor) {}

    /** Whether this reflector is ready to redirect a ray (e.g. a mirror item is installed). */
    boolean canReflect();

    /** Returns the direction the outgoing ray should travel given the incoming ray direction (single-output fallback). */
    Direction reflect(Direction incoming);

    /**
     * Fraction of heat that survives transit through this reflector (0–1).
     * For splitters this is the fraction per output (e.g. 0.5 for a lossless 50/50 split).
     */
    default float getHeatMultiplier() { return 1.0f; }

    /**
     * Returns all outgoing ray directions and their power factors for a given incoming direction.
     * Default: single output using {@link #reflect} and {@link #getHeatMultiplier}.
     * Override for multi-output blocks (splitters).
     */
    default List<RayOutput> getOutputs(Direction incoming) {
        return List.of(new RayOutput(reflect(incoming), getHeatMultiplier()));
    }

    /** Colors used to render the outgoing ray segment ([start] or [start, mid, end]). */
    int[] getBeamColors();

    /**
     * Called by the upstream emitter each tick to push the resolved outgoing segment state.
     * @param power the heat multiplier at the point this reflector was reached (0–1)
     */
    void updateRayOutput(Direction outDir, int beamLen, int pointerLen, float power);

    /** Clears the outgoing segment — called when the ray no longer reaches this reflector. */
    void clearRayOutput();
}
