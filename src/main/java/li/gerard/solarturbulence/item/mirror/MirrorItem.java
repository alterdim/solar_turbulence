package li.gerard.solarturbulence.item.mirror;

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

    abstract public void onRandomMirrorEvent();
}
