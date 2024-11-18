package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.skin.animation.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.animation.molang.runtime.SyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class AnimationEngine {

    private static final MolangVirtualMachine VM = new MolangVirtualMachine();

    public static void apply(@Nullable Object source, BakedSkin skin, SkinRenderContext context) {
        VM.beginVariableCaching();
        var animationManager = context.getAnimationManager();
        var animationContext = animationManager.getAnimationContext(skin);
        apply(source, skin, context.getPartialTicks(), context.getAnimationTicks(), animationContext, animationManager);
        VM.endVariableCaching();
    }

    public static void apply(@Nullable Object source, BakedSkin skin, float partialTick, float animationTime, AnimationContext animationContext, AnimationManager animationManager) {
        animationContext.beginUpdates(animationTime);
        for (var animationController : skin.getAnimationControllers()) {
            // query the current play state of the animation controller.
            var playState = animationContext.getPlayState(animationController);
            if (playState == null) {
                continue;
            }
            // we only bind it when transformer use the molang environment.
            var adjustedTime = playState.getAdjustedTime(animationTime);
            var executionContext = animationManager.getExecutionContext();
            if (animationController.isRequiresVirtualMachine()) {
                executionContext.upload(skin.getId(), playState.getTime(), adjustedTime, animationTime, partialTick);
            }

            // check/switch frames of animation and write to applier.
            animationController.process(adjustedTime, playState, executionContext);
        }
        animationContext.commitUpdates();
    }


    public static Expression compile(String source) throws SyntaxException {
        return VM.compile(source);
    }
}

