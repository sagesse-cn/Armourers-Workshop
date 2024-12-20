package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.math.OpenVector3f;
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


    void setSpeed(OpenVector3f speed);

    OpenVector3f getSpeed();


    void setPosition(OpenVector3f position);

    OpenVector3f getPosition();

    OpenVector3f getPositionOld();


    OpenVector3f getLocalPosition();
//                if (!particle.relativePosition) {
//                    local.sub(emitter.lastGlobal);
//                }

    OpenVector3f getLocalPositionOld();
//                if (!particle.relativePosition) {
//                    prevLocal.sub(emitter.lastGlobal);
//                }

    OpenVector3f getGlobalPosition();

    OpenVector3f getGlobalPositionOld();


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
