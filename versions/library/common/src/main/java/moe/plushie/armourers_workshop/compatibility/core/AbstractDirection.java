package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.utils.EnumMapper;
import net.minecraft.core.Direction;

@Available("[1.16, )")
public class AbstractDirection {

    private static final EnumMapper<OpenDirection, Direction> MAPPER = EnumMapper.create(OpenDirection.NORTH, Direction.NORTH, builder -> {
        builder.add(OpenDirection.DOWN, Direction.DOWN);
        builder.add(OpenDirection.UP, Direction.UP);
        builder.add(OpenDirection.NORTH, Direction.NORTH);
        builder.add(OpenDirection.SOUTH, Direction.SOUTH);
        builder.add(OpenDirection.WEST, Direction.WEST);
        builder.add(OpenDirection.EAST, Direction.EAST);
    });

    public static OpenDirection wrap(Direction direction) {
        return MAPPER.getKey(direction);
    }

    public static Direction unwrap(OpenDirection direction) {
        return MAPPER.getValue(direction);
    }
}
