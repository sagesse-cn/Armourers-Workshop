package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.math.Vector3f;

public interface SkinParticleEmitter {

//		"variable.emitter_age"
//		"variable.emitter_lifetime"
//		"variable.emitter_random_1"
//		"variable.emitter_random_2"
//		"variable.emitter_random_3"
//		"variable.emitter_random_4"


    void spawnParticle();

    void start();

    void stop();


    void setTime(double time);

    double getTime();


    void setDuration(double duration);

    double getDuration();


    void setPosition(Vector3f position);

    Vector3f getPosition();


    // default is true
    void setEmissive(boolean isEmissive);

    boolean isEmissive();


    boolean isPlaying();
}
