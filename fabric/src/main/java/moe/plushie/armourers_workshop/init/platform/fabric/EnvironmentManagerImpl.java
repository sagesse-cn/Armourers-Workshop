package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compatibility.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricRegistryManager;
import moe.plushie.armourers_workshop.init.environment.EnvironmentPlatformType;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.ConfigBuilderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.util.Optional;

@SuppressWarnings("unused")
public class EnvironmentManagerImpl {

    private static MinecraftServer CURRENT_SERVER;

    public static String getModVersion(String modId) {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modId);
        return container.map(modContainer -> modContainer.getMetadata().getVersion().toString()).orElse(null);
    }

    public static EnvironmentType getEnvironmentType() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            return EnvironmentType.SERVER;
        }
        return EnvironmentType.CLIENT;
    }

    public static File getRootDirectory() {
        return new File(FabricLoader.getInstance().getGameDir().toFile(), "armourers_workshop");
    }

    public static MinecraftServer getServer() {
        return CURRENT_SERVER;
    }

    public static boolean isDevelopment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static IConfigSpec getClientConfigSpec() {
        return ConfigBuilderImpl.createClientSpec();
    }

    public static IConfigSpec getCommonConfigSpec() {
        return ConfigBuilderImpl.createCommonSpec();
    }

    public static void attach(MinecraftServer server) {
        CURRENT_SERVER = server;
    }

    public static void detach(MinecraftServer server) {
        CURRENT_SERVER = null;
    }

    public static boolean isInstalled(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static EnvironmentPlatformType getPlatformType() {
        return EnvironmentPlatformType.FABRIC;
    }

    public static AbstractRegistryManager getRegistryManager() {
        return AbstractFabricRegistryManager.INSTANCE;
    }
}
