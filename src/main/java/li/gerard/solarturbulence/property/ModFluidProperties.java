package li.gerard.solarturbulence.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ModFluidProperties(float conductivity) {
    public static final Codec<ModFluidProperties> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.FLOAT.fieldOf("conductivity").forGetter(ModFluidProperties::conductivity)
    ).apply(inst, ModFluidProperties::new));
}