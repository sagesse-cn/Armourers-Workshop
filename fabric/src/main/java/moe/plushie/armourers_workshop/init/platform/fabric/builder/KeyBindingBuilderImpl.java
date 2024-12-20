package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricKeyMapping;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import moe.plushie.armourers_workshop.utils.OpenKeyModifier;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.function.Supplier;

public class KeyBindingBuilderImpl<T extends IKeyBinding> implements IKeyBindingBuilder<T> {

    private static final ArrayList<Pair<KeyMapping, Supplier<Runnable>>> INPUTS = createAndAttach();

    private final String key;
    private IKeyModifier modifier = OpenKeyModifier.NONE;
    private String category = "";
    private Supplier<Runnable> handler;

    public KeyBindingBuilderImpl(String key) {
        this.key = key;
    }

    @Override
    public IKeyBindingBuilder<T> modifier(IKeyModifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public IKeyBindingBuilder<T> category(String category) {
        this.category = category;
        return this;
    }

    @Override
    public IKeyBindingBuilder<T> bind(Supplier<Runnable> handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public T build(String name) {
        var nameKey = "keys.armourers_workshop." + name;
        var categoryKey = "keys.armourers_workshop." + category;
        var input = InputConstants.getKey(key);
        var binding = createKeyBinding(nameKey, input, categoryKey);
        if (handler != null) {
            INPUTS.add(Pair.of(binding, handler));
        }
        AbstractFabricKeyMapping.register(name, binding);
        return Objects.unsafeCast(new IKeyBinding() {

            @Override
            public Component getKeyName() {
                return binding.getTranslatedKeyMessage();
            }

            @Override
            public IKeyModifier getKeyModifier() {
                return OpenKeyModifier.NONE;
            }
        });
    }

    public static class OnceKeyBinding extends AbstractFabricKeyMapping {

        // Once consumed, must need to release the key to reset this flags.
        private boolean canConsumeClick = true;

        public OnceKeyBinding(String string, InputConstants.Key key, String string2) {
            super(string, key.getType(), key.getValue(), string2);
        }

        @Override
        public boolean consumeClick() {
            if (canConsumeClick && isDown()) {
                canConsumeClick = false;
                return true;
            }
            return false;
        }

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (!isDown) {
                canConsumeClick = true;
            }
        }
    }

    private static OnceKeyBinding createKeyBinding(String description, InputConstants.Key keyCode, String category) {
        return new OnceKeyBinding(description, keyCode, category);
    }

    private static <T> ArrayList<T> createAndAttach() {
        // attach the input event to client.
        EventBus.register(RenderFrameEvent.Post.class, event -> INPUTS.forEach(pair -> {
            if (pair.getKey().consumeClick()) {
                pair.getValue().get().run();
            }
        }));
        return new ArrayList<>();
    }
}
