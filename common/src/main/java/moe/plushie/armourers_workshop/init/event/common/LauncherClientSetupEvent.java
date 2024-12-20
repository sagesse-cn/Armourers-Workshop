package moe.plushie.armourers_workshop.init.event.common;

public interface LauncherClientSetupEvent {

    void enqueueWork(Runnable work);
}
