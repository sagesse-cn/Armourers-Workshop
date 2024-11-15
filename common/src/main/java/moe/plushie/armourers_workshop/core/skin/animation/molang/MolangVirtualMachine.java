package moe.plushie.armourers_workshop.core.skin.animation.molang;

import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Constant;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Variable;
import moe.plushie.armourers_workshop.core.skin.animation.molang.impl.Compiler;
import moe.plushie.armourers_workshop.core.skin.animation.molang.impl.SyntaxException;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class MolangVirtualMachine {

    private static final MolangVirtualMachine DEFAULT = new MolangVirtualMachine();

    private final Compiler compiler = new Compiler();

    private final Map<String, Variable> variables = new ConcurrentHashMap<>();

    public final Variable animTime = register("query.anim_time");

    public final Variable lifeTime = register("query.life_time");

    public final Variable actorCount = register("query.actor_count");

    public final Variable timeOfDay = register("query.time_of_day");

    public final Variable moonPhase = register("query.moon_phase");

    public final Variable distanceFromCamera = register("query.distance_from_camera");

    public final Variable isOnGround = register("query.is_on_ground");

    public final Variable isInWater = register("query.is_in_water");

    public final Variable isInWaterOrRain = register("query.is_in_water_or_rain");

    public final Variable health = register("query.health");

    public final Variable maxHealth = register("query.max_health");

    public final Variable isOnFire = register("query.is_on_fire");

    public final Variable groundSpeed = register("query.ground_speed");

    public final Variable yawSpeed = register("query.yaw_speed");

    public final Variable headPitch = register("aw.head_pitch");

    public static MolangVirtualMachine get() {
        return DEFAULT;
    }

    /**
     * Create a molang expression
     */
    public Expression eval(String source) throws SyntaxException {
        var expr = compiler.compile(source);
        if (ModConfig.Client.enableMolangDebug && !(expr instanceof Constant)) {
            ModLog.debug("source: {}", source);
            ModLog.debug("compiled: {}", expr);
        }
        return expr;
    }

    public Variable register(String name) {
        var variable = new Variable(name);
        compiler.registerVariable(name, variable);
        variables.put(name, variable);
        return variable;
    }

    public Map<String, ? extends Variable> getVariables() {
        return variables;
    }
}
