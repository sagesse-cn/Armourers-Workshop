package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.nbt.CompoundTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ModMenuOptions {

    private static final ModMenuOptions INSTANCE = new ModMenuOptions();

    private final File contentPath;
    private final CompoundTag values = new CompoundTag();

    private ModMenuOptions() {
        contentPath = new File(EnvironmentManager.getRootDirectory(), "options.dat");
        if (contentPath.exists()) {
            load();
        }
    }

    public static ModMenuOptions getInstance() {
        return INSTANCE;
    }

    public void putString(String key, String value) {
        values.putString(key, value);
        setChanged();
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        if (values.contains(key)) {
            return values.getString(key);
        }
        return defaultValue;
    }

    public void putInt(String key, int value) {
        values.putInt(key, value);
        setChanged();
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        if (values.contains(key)) {
            return values.getInt(key);
        }
        return defaultValue;
    }

    public void putBoolean(String key, boolean value) {
        values.putBoolean(key, value);
        setChanged();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (values.contains(key)) {
            return values.getBoolean(key);
        }
        return defaultValue;
    }

    public void putTag(String key, CompoundTag tag) {
        values.put(key, tag);
        setChanged();
    }

    public CompoundTag getTag(String key) {
        if (values.contains(key, Constants.TagFlags.COMPOUND)) {
            return values.getCompound(key);
        }
        return null;
    }

    private void load() {
        try (var inputStream = new FileInputStream(contentPath)) {
            var tag = TagSerializer.parse(inputStream);
            values.merge(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (var outputStream = new FileOutputStream(contentPath)) {
            TagSerializer.writeToStream(values, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setChanged() {
        EnvironmentExecutor.runOnBackground(() -> this::save);
    }
}

