package moe.plushie.armourers_workshop.init.event.client;

import com.apple.library.coregraphics.CGGraphicsContext;

public interface RenderScreenEvent {

    CGGraphicsContext getContext();

    interface Pre extends RenderScreenEvent {

    }

    interface Post extends RenderScreenEvent {
    }
}
