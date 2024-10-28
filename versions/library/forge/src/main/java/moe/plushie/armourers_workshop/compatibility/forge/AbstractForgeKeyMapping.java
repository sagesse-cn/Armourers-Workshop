package moe.plushie.armourers_workshop.compatibility.forge;

import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterKeyMappingsEvent;
import moe.plushie.armourers_workshop.utils.OpenKeyModifier;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Available("[1.21, )")
public abstract class AbstractForgeKeyMapping extends KeyMapping {

    private static final HashMap<InputConstants.Key, ArrayList<KeyMapping>> MAPPINGS = new HashMap<>();

    private static final Map<IKeyModifier, KeyModifier> MODIFIERS1 = Collections.immutableMap(builder -> {
        builder.put(OpenKeyModifier.CONTROL, KeyModifier.CONTROL);
        builder.put(OpenKeyModifier.SHIFT, KeyModifier.SHIFT);
        builder.put(OpenKeyModifier.ALT, KeyModifier.ALT);
        builder.put(OpenKeyModifier.NONE, KeyModifier.NONE);
    });

    private static final Map<KeyModifier, IKeyModifier> MODIFIERS2 = Collections.immutableMap(builder -> {
        builder.put(KeyModifier.CONTROL, OpenKeyModifier.CONTROL);
        builder.put(KeyModifier.SHIFT, OpenKeyModifier.SHIFT);
        builder.put(KeyModifier.ALT, OpenKeyModifier.ALT);
        builder.put(KeyModifier.NONE, OpenKeyModifier.NONE);
    });

    public AbstractForgeKeyMapping(String description, IKeyModifier keyModifier, InputConstants.Key keyCode, String category) {
        super(description, KeyConflictContext.IN_GAME, MODIFIERS1.getOrDefault(keyModifier, KeyModifier.NONE), keyCode, category);
        bind(keyCode, this);
    }

    public static void register(String key, KeyMapping keyMapping) {
        EventManager.listen(RegisterKeyMappingsEvent.class, event -> event.register(keyMapping));
    }

    private static void bind(InputConstants.Key keyCode, KeyMapping keyMapping) {
        MAPPINGS.computeIfAbsent(keyCode, k -> new ArrayList<>()).add(keyMapping);
    }

    private static void unbind(InputConstants.Key keyCode, KeyMapping keyMapping) {
        var mappings = MAPPINGS.get(keyCode);
        if (mappings != null) {
            mappings.remove(keyMapping);
        }
    }

    @Nullable
    public static List<KeyMapping> findKeysByCode(InputConstants.Key keyCode) {
        // fix the neo forge wrong active modifier by `ctrl-shift-<key>`
        var mappings = MAPPINGS.get(keyCode);
        if (mappings != null) {
            return mappings.stream()
                    .filter(binding -> binding.isActiveAndMatches(keyCode))
                    .toList();
        }
        return null;
    }

    @Override
    public boolean consumeClick() {
        return super.consumeClick();
    }

    public IKeyModifier getOpenKeyModifier() {
        return MODIFIERS2.getOrDefault(getKeyModifier(), OpenKeyModifier.NONE);
    }

    @Override
    public void setKeyModifierAndCode(KeyModifier keyModifier, InputConstants.Key keyCode) {
        unbind(getKey(), this);
        super.setKeyModifierAndCode(keyModifier, keyCode);
        bind(getKey(), this);
    }
}
