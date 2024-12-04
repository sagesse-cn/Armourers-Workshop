package moe.plushie.armourers_workshop.init.platform.event.client;

import moe.plushie.armourers_workshop.compatibility.core.AbstractDeltaTracker;

public interface RenderFrameEvent {

    AbstractDeltaTracker getDeltaTracker();

    interface Pre extends RenderFrameEvent {

    }

    interface Post extends RenderFrameEvent {
    }
}
