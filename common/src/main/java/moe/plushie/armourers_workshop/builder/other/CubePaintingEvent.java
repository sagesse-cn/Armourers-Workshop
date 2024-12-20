package moe.plushie.armourers_workshop.builder.other;

import moe.plushie.armourers_workshop.api.common.IPaintable;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.skin.texture.ISkinPaintColor;
import moe.plushie.armourers_workshop.builder.blockentity.BoundingBoxBlockEntity;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolSelector;
import moe.plushie.armourers_workshop.core.data.color.TexturedPaintColor;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;

public class CubePaintingEvent {

    private static final HashMap<Class<? extends Action>, ActionTypes> REVERSE_LOOKUP = new HashMap<>();

    final IPaintToolAction action;
    final IPaintToolSelector selector;
    final HashMap<Pair<BlockPos, Direction>, IPaintToolAction> overrides = new HashMap<>();

    private int targetCount = 0;

    public CubePaintingEvent(IPaintToolSelector selector, IPaintToolAction action) {
        this.selector = selector;
        this.action = action;
    }

    public CubePaintingEvent(IFriendlyByteBuf buffer) {
        this.selector = CubeSelector.from(buffer);
        this.action = Action.fromBuffer(buffer);
        // read and remapping actions.
        while (true) {
            var size = buffer.readByte();
            if (size <= 0) {
                break;
            }
            var pos = buffer.readBlockPos();
            for (var i = 0; i < size; ++i) {
                var dir = buffer.readEnum(Direction.class);
                var action = Action.fromBuffer(buffer);
                overrides.put(Pair.of(pos, dir), action);
            }
        }
    }

    public void encode(IFriendlyByteBuf buffer) {
        this.selector.encode(buffer);
        Action.writeBuffer(action, buffer);
        // sort and write actions.
        HashMap<BlockPos, HashMap<Direction, IPaintToolAction>> sorted = new HashMap<>();
        this.overrides.forEach((pair, action) -> sorted.computeIfAbsent(pair.getKey(), k -> new HashMap<>()).put(pair.getValue(), action));
        sorted.forEach((pos, items) -> {
            buffer.writeByte(items.size());
            buffer.writeBlockPos(pos);
            items.forEach((dir, action) -> {
                buffer.writeEnum(dir);
                Action.writeBuffer(action, buffer);
            });
        });
        buffer.writeByte(0);
    }

    public boolean prepare(CubeChangesCollector collector, UseOnContext context) {
        var level = context.getLevel();
        var player = context.getPlayer();
        selector.forEach(context, (target, dir) -> {
            var cube = collector.getCube(target);
            if (cube.is(IPaintable.class)) {
                targetCount += 1;
            }
            var action1 = action.build(level, target, dir, cube, player);
            if (action1 != action) {
                overrides.put(Pair.of(target, dir), action1);
            }
        });
        return targetCount != 0;
    }

    public void apply(CubeChangesCollector collector, UseOnContext context) {
        var level = context.getLevel();
        var player = context.getPlayer();
        selector.forEach(context, (target, dir) -> {
            var action1 = overrides.getOrDefault(Pair.of(target, dir), action);
            action1.apply(level, target, dir, collector.getCube(target), player);
        });
    }

    public static abstract class Action implements IPaintToolAction {

        public static Action fromBuffer(IFriendlyByteBuf buffer) {
            var type = buffer.readEnum(ActionTypes.class);
            return type.factory.apply(buffer);
        }

        public static void writeBuffer(IPaintToolAction action, IFriendlyByteBuf buffer) {
            buffer.writeEnum(ActionTypes.getType(action.getClass()));
            action.encode(buffer);
        }

        @Override
        public abstract void apply(Level level, BlockPos pos, Direction dir, IPaintable provider, @Nullable Player player);

        @Override
        public Action build(Level level, BlockPos pos, Direction dir, IPaintable provider, @Nullable Player player) {
            return this;
        }

        public abstract void encode(final IFriendlyByteBuf buffer);
    }

    public static abstract class MixedAction extends Action {

        public abstract ISkinPaintColor resolve(BlockPos pos, Direction dir, ISkinPaintColor sourceColor);

        @Override
        public Action build(Level level, BlockPos pos, Direction dir, IPaintable provider, @Nullable Player player) {
            if (provider.shouldChangeColor(dir)) {
                var paintColor = provider.getColor(dir);
                if (paintColor instanceof TexturedPaintColor) {
                    // for bounding box block, the client side maybe used texture color,
                    // this is inconsistent with the server side.
                    // so for matching user looked the color, we need to generate
                    // the real color on the client side and then sent to server side.
                    return new SetAction(resolve(pos, dir, paintColor));
                }
            }
            return this;
        }

        @Override
        public void apply(Level level, BlockPos pos, Direction dir, IPaintable provider, @Nullable Player player) {
            if (provider.shouldChangeColor(dir)) {
                provider.setColor(dir, resolve(pos, dir, provider.getColor(dir)));
            }
        }
    }

    public static class SetAction extends Action {

        final ISkinPaintColor destinationColor;

        final boolean usePaintColor;
        final boolean usePaintType;

        public SetAction(ISkinPaintColor paintColor) {
            this(paintColor, true, true);
        }

        public SetAction(ISkinPaintColor paintColor, boolean usePaintColor, boolean usePaintType) {
            this.destinationColor = paintColor;
            this.usePaintColor = usePaintColor;
            this.usePaintType = usePaintType;
        }

        public SetAction(IFriendlyByteBuf buffer) {
            this.destinationColor = SkinPaintColor.of(buffer.readInt());
            this.usePaintColor = buffer.readBoolean();
            this.usePaintType = buffer.readBoolean();
        }

        @Override
        public void encode(IFriendlyByteBuf buffer) {
            buffer.writeInt(destinationColor.getRawValue());
            buffer.writeBoolean(usePaintColor);
            buffer.writeBoolean(usePaintType);
        }

        @Override
        public void apply(Level level, BlockPos pos, Direction dir, IPaintable provider, @Nullable Player player) {
            if (provider.shouldChangeColor(dir)) {
                provider.setColor(dir, resolve(pos, dir, provider));
            }
        }

        public ISkinPaintColor resolve(BlockPos pos, Direction dir, IPaintable provider) {
            // when no needs processing required, ignore.
            if (usePaintType && usePaintColor) {
                return destinationColor;
            }
            var oldValue = provider.getColor(dir);
            if (usePaintColor) {
                return destinationColor.withPaintType(oldValue.getPaintType());
            }
            if (usePaintType) {
                return destinationColor.withColor(oldValue.getRed());
            }
            return oldValue;
        }
    }

    public static class ClearAction extends Action {

        public ClearAction() {
        }

        public ClearAction(IFriendlyByteBuf buffer) {
        }

        @Override
        public void encode(IFriendlyByteBuf buffer) {
        }

        @Override
        public void apply(Level level, BlockPos pos, Direction dir, IPaintable provider, @Nullable Player player) {
            if (provider.shouldChangeColor(dir)) {
                if (level.getBlockEntity(pos) instanceof BoundingBoxBlockEntity) {
                    provider.setColor(dir, SkinPaintColor.CLEAR);
                } else {
                    provider.setColor(dir, SkinPaintColor.WHITE);
                }
            }
        }
    }

    public static class BrightnessAction extends MixedAction {

        final int intensity;

        public BrightnessAction(int intensity) {
            this.intensity = intensity;
        }

        public BrightnessAction(IFriendlyByteBuf buffer) {
            this.intensity = buffer.readInt();
        }

        @Override
        public void encode(IFriendlyByteBuf buffer) {
            buffer.writeInt(intensity);
        }

        @Override
        public ISkinPaintColor resolve(BlockPos pos, Direction dir, ISkinPaintColor sourceColor) {
            int rgb = sourceColor.getRGB();
            rgb = ColorUtils.makeColorBighter(rgb, intensity);
            return sourceColor.withColor(rgb);
        }
    }

    public static class NoiseAction extends MixedAction {

        final int intensity;
        final boolean isShadeOnly;
        final int seed;
        final Random random = new Random();

        public NoiseAction(int intensity, boolean isShadeOnly) {
            this.intensity = intensity;
            this.isShadeOnly = isShadeOnly;
            this.seed = random.nextInt();
        }

        public NoiseAction(IFriendlyByteBuf buffer) {
            this.intensity = buffer.readInt();
            this.isShadeOnly = buffer.readBoolean();
            this.seed = buffer.readInt();
        }

        @Override
        public void encode(IFriendlyByteBuf buffer) {
            buffer.writeInt(intensity);
            buffer.writeBoolean(isShadeOnly);
            buffer.writeInt(seed);
        }

        @Override
        public ISkinPaintColor resolve(BlockPos pos, Direction dir, ISkinPaintColor sourceColor) {
            int rgb = sourceColor.getRGB();
            if (this.isShadeOnly) {
                rgb = ColorUtils.addShadeNoise(rgb, intensity, getRandom(pos, dir));
            } else {
                rgb = ColorUtils.addColorNoise(rgb, intensity, getRandom(pos, dir));
            }
            return sourceColor.withColor(rgb);
        }

        private Random getRandom(BlockPos pos, Direction dir) {
            // this needs to be executed on different computers,
            // so we need to make same generate the random results.
            random.setSeed(pos.asLong() + ((long) seed << dir.ordinal()));
            return random;
        }
    }

    public static class HueAction extends MixedAction {

        final ISkinPaintColor destinationColor;

        final boolean changeHue;
        final boolean changeSaturation;
        final boolean changeBrightness;
        final boolean changePaintType;

        public HueAction(ISkinPaintColor paintColor, boolean hue, boolean saturation, boolean brightness, boolean paintType) {
            this.destinationColor = paintColor;
            this.changeHue = hue;
            this.changeSaturation = saturation;
            this.changeBrightness = brightness;
            this.changePaintType = paintType;
        }

        public HueAction(IFriendlyByteBuf buffer) {
            this.destinationColor = SkinPaintColor.of(buffer.readInt());
            this.changeHue = buffer.readBoolean();
            this.changeSaturation = buffer.readBoolean();
            this.changeBrightness = buffer.readBoolean();
            this.changePaintType = buffer.readBoolean();
        }

        @Override
        public void encode(IFriendlyByteBuf buffer) {
            buffer.writeInt(destinationColor.getRawValue());
            buffer.writeBoolean(changeHue);
            buffer.writeBoolean(changeSaturation);
            buffer.writeBoolean(changeBrightness);
            buffer.writeBoolean(changePaintType);
        }

        @Override
        public ISkinPaintColor resolve(BlockPos pos, Direction dir, ISkinPaintColor sourceColor) {
            float[] sourceHSB = ColorUtils.RGBtoHSB(sourceColor.getRGB());
            float[] destinationHSB = ColorUtils.RGBtoHSB(destinationColor.getRGB());
            if (!changeHue) {
                destinationHSB[0] = sourceHSB[0];
            }
            if (!changeSaturation) {
                destinationHSB[1] = sourceHSB[1];
            }
            if (!changeBrightness) {
                destinationHSB[2] = sourceHSB[2];
            }
            int rgb = ColorUtils.HSBtoRGB(destinationHSB);
            if (!changePaintType) {
                return sourceColor.withColor(rgb);
            }
            return destinationColor.withColor(rgb);
        }
    }

    public static class BlendingAction extends MixedAction {

        final int intensity;
        final ISkinPaintColor destinationColor;

        public BlendingAction(ISkinPaintColor destinationColor, int intensity) {
            this.destinationColor = destinationColor;
            this.intensity = intensity;
        }

        public BlendingAction(IFriendlyByteBuf buffer) {
            this.destinationColor = SkinPaintColor.of(buffer.readInt());
            this.intensity = buffer.readInt();
        }

        @Override
        public void encode(IFriendlyByteBuf buffer) {
            buffer.writeInt(destinationColor.getRawValue());
            buffer.writeInt(intensity);
        }

        @Override
        public ISkinPaintColor resolve(BlockPos pos, Direction dir, ISkinPaintColor sourceColor) {
            int destRGB = destinationColor.getRGB();
            int destR = ColorUtils.getRed(destRGB);
            int destG = ColorUtils.getGreen(destRGB);
            int destB = ColorUtils.getBlue(destRGB);

            int sourceRGB = sourceColor.getRGB();
            int oldR = ColorUtils.getRed(sourceRGB);
            int oldG = ColorUtils.getGreen(sourceRGB);
            int oldB = ColorUtils.getBlue(sourceRGB);

            float newR = destR / 100F * intensity;
            newR += oldR / 100F * (100 - intensity);
            newR = OpenMath.clamp((int) newR, 0, 255);

            float newG = destG / 100F * intensity;
            newG += oldG / 100F * (100 - intensity);
            newG = OpenMath.clamp((int) newG, 0, 255);

            float newB = destB / 100F * intensity;
            newB += oldB / 100F * (100 - intensity);
            newB = OpenMath.clamp((int) newB, 0, 255);

            return sourceColor.withColor((int) newR, (int) newG, (int) newB);
        }
    }

    public enum ActionTypes {

        SET_COLOR(SetAction.class, SetAction::new),
        SET_BRIGHTNESS(BrightnessAction.class, BrightnessAction::new),
        SET_NOISE(NoiseAction.class, NoiseAction::new),
        SET_HUE(HueAction.class, HueAction::new),
        SET_BLENDING(BlendingAction.class, BlendingAction::new),
        CLEAR_COLOR(ClearAction.class, ClearAction::new);

        private final Function<IFriendlyByteBuf, Action> factory;

        ActionTypes(Class<? extends Action> packetClass, Function<IFriendlyByteBuf, Action> factory) {
            this.factory = factory;
            REVERSE_LOOKUP.put(packetClass, this);
        }

        public static ActionTypes getType(final Class<?> c) {
            return REVERSE_LOOKUP.get(c);
        }
    }
}


