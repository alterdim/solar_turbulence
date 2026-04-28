package li.gerard.solarturbulence.block.generic;

import net.minecraft.core.Direction;

/**
 * Implemented by any block entity that can intercept and redirect a heat ray.
 * The ray system queries this interface; concrete mirror behaviour lives in MirrorItem.
 */
public interface IRayReflector {

    /** Whether this reflector is ready to redirect a ray (e.g. a mirror item is installed). */
    boolean canReflect();

    /** Returns the direction the outgoing ray should travel given the incoming ray direction. */
    Direction reflect(Direction incoming);

    /**
     * Fraction of heat that survives transit through this reflector (0–1).
     * Default: 1.0 (no loss). Implementations should override to define their actual efficiency.
     */
    default float getHeatMultiplier() { return 1.0f; }

    /** Colors used to render the outgoing ray segment ([start] or [start, mid, end]). */
    int[] getBeamColors();

    /** Called by the upstream emitter each tick to push the resolved outgoing segment state. */
    void updateRayOutput(Direction outDir, int beamLen, int pointerLen);

    /** Clears the outgoing segment — called when the ray no longer reaches this reflector. */
    void clearRayOutput();
}
