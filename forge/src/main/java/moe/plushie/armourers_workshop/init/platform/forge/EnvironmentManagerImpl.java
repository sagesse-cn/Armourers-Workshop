package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.forge.builder.ConfigBuilderImpl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.io.File;

@SuppressWarnings("unused")
public class EnvironmentManagerImpl {

    public static String getVersion() {
        ModFileInfo fileInfo = ModList.get().getModFileById(ArmourersWorkshop.MOD_ID);
        if (fileInfo != null && fileInfo.getMods().size() != 0) {
            ArtifactVersion version = fileInfo.getMods().get(0).getVersion();
            return version.toString();
        }
        return "Unknown";
    }

    public static EnvironmentType getEnvironmentType() {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            return EnvironmentType.SERVER;
        }
        return EnvironmentType.CLIENT;
    }

    public static File getRootDirectory() {
        return new File(FMLPaths.GAMEDIR.get().toFile(), "armourers_workshop");
    }

    public static boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    public static IConfigSpec getClientConfigSpec() {
        return ConfigBuilderImpl.createClientSpec();
    }

    public static IConfigSpec getCommonConfigSpec() {
        return ConfigBuilderImpl.createCommonSpec();
    }
}
