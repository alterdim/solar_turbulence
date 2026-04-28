package li.gerard.solarturbulence.item.mirror;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public abstract class MirrorItem extends Item {

    private final int solarGeneration;
    private final ResourceLocation mirrorFaceTexture;
    private final int[] beamColors;

    public MirrorItem(Properties properties, ResourceLocation mirrorFaceTexture, int solarGeneration, int... beamColors) {
        super(properties);
        this.mirrorFaceTexture = mirrorFaceTexture;
        this.solarGeneration = solarGeneration;
        if (beamColors.length < 1 || beamColors.length > 3) {
            throw new IllegalArgumentException("MirrorItem requires 1 to 3 beam colors, got " + beamColors.length);
        }
        this.beamColors = beamColors;
    }

    public ResourceLocation getMirrorFaceTexture() {
        return mirrorFaceTexture;
    }

    public int[] getBeamColors() { return beamColors; }

    /**
     * Fraction of heat that survives transit through this mirror (0–1).
     * Default: 0.85 (15% loss for a basic copper mirror).
     * Override to define a lossless or more/less efficient mirror type.
     */
    public float getHeatMultiplier() { return 0.85f; }

    /**
     * Determines the outgoing ray direction after reflection.
     * Default: the beam exits in the mirror's facing direction regardless of incidence angle.
     * Override for physics-based or special reflection rules.
     */
    public Direction reflect(Direction incoming, Direction mirrorFacing) {
        return mirrorFacing;
    }

    abstract public void onRandomMirrorEvent();
}
