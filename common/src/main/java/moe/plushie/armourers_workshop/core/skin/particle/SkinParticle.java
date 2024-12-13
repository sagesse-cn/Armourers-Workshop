package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.math.Vector3f;
import net.minecraft.world.level.block.Block;

public interface SkinParticle {

//    	"variable.particle_age"
//		"variable.particle_lifetime"
//		"variable.particle_random_1"
//		"variable.particle_random_2"
//		"variable.particle_random_3"
//		"variable.particle_random_4"

    void setFlags(boolean position, boolean rotation, boolean velocity);

    void setColor(float r, float g, float b, float a);


    void kill();


    void setTime(double time);

    double getTime();


    void setDuration(double duration);

    double getDuration();


    void setSpeed(Vector3f speed);

    Vector3f getSpeed();


    void setPosition(Vector3f position);

    Vector3f getPosition();

    Vector3f getPositionOld();


    Vector3f getLocalPosition();
//                if (!particle.relativePosition) {
//                    local.sub(emitter.lastGlobal);
//                }

    Vector3f getLocalPositionOld();
//                if (!particle.relativePosition) {
//                    prevLocal.sub(emitter.lastGlobal);
//                }

    Vector3f getGlobalPosition();

    Vector3f getGlobalPositionOld();


    Block getBlock();
//    public IBlock getBlock(BedrockEmitter emitter, BedrockParticle particle) {
//        if (emitter.world == null) {
//            return IBlock.Blocks.AIR;
//        }
//        Vector3d position = particle.getGlobalPosition(emitter);
//        this.pos.setPos(position.getX(), position.getY(), position.getZ());
//        return emitter.world.getBlockAtPos(this.pos);
//    }

    boolean isAlive();
//    if (particle.dead || emitter.world == null) {
//        return;
//    }
}
