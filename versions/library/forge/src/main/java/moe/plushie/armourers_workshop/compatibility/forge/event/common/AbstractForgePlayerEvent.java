package moe.plushie.armourers_workshop.compatibility.forge.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.event.common.PlayerEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Available("[1.19, )")
public class AbstractForgePlayerEvent {

    public static IEventHandler<PlayerEvent.LoggingIn> loggingInFactory() {
        return AbstractForgeCommonEventsImpl.PLAYER_LOGIN.map(event -> event::getEntity);
    }

    public static IEventHandler<PlayerEvent.LoggingOut> loggingOutFactory() {
        return AbstractForgeCommonEventsImpl.PLAYER_LOGOUT.map(event -> event::getEntity);
    }


    public static IEventHandler<PlayerEvent.Clone> cloneFactory() {
        return AbstractForgeCommonEventsImpl.PLAYER_CLONE.map(event -> new PlayerEvent.Clone() {
            @Override
            public Player getOriginal() {
                return event.getOriginal();
            }

            @Override
            public Player getPlayer() {
                return event.getEntity();
            }
        });
    }

    public static IEventHandler<PlayerEvent.Death> deathFactory() {
        return AbstractForgeCommonEventsImpl.PLAYER_DEATH.flatMap(event -> {
            if (event.getEntity() instanceof Player player) {
                return () -> player;
            }
            return null;
        });
    }


    public static IEventHandler<PlayerEvent.Attack> attackFactory() {
        return AbstractForgeCommonEventsImpl.PLAYER_ATTACK.map(event -> new PlayerEvent.Attack() {
            @Override
            public Entity getTarget() {
                return event.getTarget();
            }

            @Override
            public Player getPlayer() {
                return event.getEntity();
            }

            @Override
            public void setCancelled(boolean isCancelled) {
                event.setCanceled(isCancelled);
            }
        });
    }

    public static IEventHandler<PlayerEvent.StartTracking> startTrackingFactory() {
        return AbstractForgeCommonEventsImpl.PLAYER_TRACKING.map(event -> new PlayerEvent.StartTracking() {
            @Override
            public Entity getTarget() {
                return event.getTarget();
            }

            @Override
            public Player getPlayer() {
                return event.getEntity();
            }
        });
    }
}
