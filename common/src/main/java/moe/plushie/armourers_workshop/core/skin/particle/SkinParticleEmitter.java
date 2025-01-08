package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;

import java.util.List;

public interface SkinParticleEmitter {

//		"variable.emitter_age"
//		"variable.emitter_lifetime"
//		"variable.emitter_random_1"
//		"variable.emitter_random_2"
//		"variable.emitter_random_3"
//		"variable.emitter_random_4"

    void start();

    void stop();


    void spawnParticle();


    void setTime(double time);

    double getTime();


    void setDuration(double duration);

    double getDuration();


    void setPosition(OpenVector3f position);

    OpenVector3f getPosition();

    List<? extends SkinParticle> getParticles();

    // default is true
    void setEmissive(boolean isEmissive);

    boolean isEmissive();

    boolean isRunning();
}
