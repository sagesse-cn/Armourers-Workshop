package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.data.DataManager;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.utils.OpenDistributionType;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.network.ServerReplayPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Compatible of Replay Mod
 */
public class ReplayManager {

    private static boolean IS_REPLAYING = false;
    private static ArrayList<Runnable> REPLAY_CLEANER = new ArrayList<>();

    public static void init() {
    }

    public static void accept(ServerReplayPacket packet) {
        // in normal mode, that events have no effect.
        // in replay mode, when replay start/dragging timeline,
        // all packets will to be replayed,
        if (!IS_REPLAYING) {
            return;
        }
        switch (packet.getEvent()) {
            case START_RECORDING: {
                clean();
                // if record an integrated server, we need to launch the data service.
                var param = IFriendlyByteBuf.wrap(packet.getParameters());
                var distType = param.readEnum(OpenDistributionType.class);
                if (distType.isIntegratedServer()) {
                    var dbPath = new File(param.readUtf());
                    if (dbPath.exists()) {
                        DataManager.getInstance().connect(dbPath);
                        REPLAY_CLEANER.add(DataManager.getInstance()::disconnect);
                    } else {
                        ModLog.warn("replay skin-database missing");
                    }
                }
                // prepare and launch the skin loader.
                SkinLoader.getInstance().prepare(distType);
                SkinLoader.getInstance().start();
                REPLAY_CLEANER.add(() -> SkinLoader.getInstance().stop());
                break;
            }
            case STOP_RECORDING: {
                clean();
                break;
            }
        }
    }

    public static void startReplay() {
        IS_REPLAYING = true;
    }

    public static void stopReplay() {
        IS_REPLAYING = false;
        clean();
    }

    public static void startRecording(MinecraftServer server, Player player) {
        sendTo(player, ServerReplayPacket.Event.START_RECORDING, buf -> {
            // when this an integrated server owner,
            // we need to save path of the skin-database,
            if (server.isSingleplayer() && server.isSingleplayerOwner(player.getGameProfile())) {
                // we need restore the skin-database when replaying.
                buf.writeEnum(OpenDistributionType.INTEGRATED_SERVER);
                buf.writeUtf(EnvironmentManager.getSkinDatabaseDirectory().getPath());
                return;
            }
            // we specified replay as client mode, it will work based skin-cache.
            buf.writeEnum(OpenDistributionType.CLIENT);
        });
    }

    public static void stopRecording(MinecraftServer server, Player player) {
        sendTo(player, ServerReplayPacket.Event.STOP_RECORDING, null);
    }

    private static void sendTo(Player player, ServerReplayPacket.Event event, Consumer<IFriendlyByteBuf> consumer) {
        NetworkManager.sendTo(new ServerReplayPacket(event, consumer), (ServerPlayer) player);
    }

    private static void clean() {
        REPLAY_CLEANER.forEach(Runnable::run);
        REPLAY_CLEANER.clear();
    }
}
