package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.resources.sounds.Sound;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractSimpleSound;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.util.valueproviders.ConstantFloat;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.ThisClass;

@Available("[1.21, )")
@Extension
public class Constructor {

    public static @Self Sound create(@ThisClass Class<?> clazz, AbstractSimpleSound sound) {
        var location = sound.getId();
        var volume = ConstantFloat.of(sound.getVolume());
        var pitch = ConstantFloat.of(sound.getPitch());
        var weight = sound.getWeight();
        var attenuationDistance = sound.getAttenuationDistance();
        return new Sound(location, volume, pitch, weight, Sound.Type.FILE, false, false, attenuationDistance);
    }
}
