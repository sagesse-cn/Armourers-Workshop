package moe.plushie.armourers_workshop.core.skin.animation.molang.core;

import moe.plushie.armourers_workshop.core.skin.animation.molang.impl.FlowControllable;
import moe.plushie.armourers_workshop.core.skin.animation.molang.impl.FlowController;

import java.util.List;

// loop(<count>, <expression>);
public final class Loop extends Function implements FlowControllable {

    private final Expression count;
    private final Expression body;
    private final FlowController controller;

    public Loop(String name, List<Expression> arguments) {
        super(name, 2, arguments);
        this.count = arguments.get(0);
        this.body = arguments.get(1);
        this.controller = FlowController.enumerate(body);
    }

    @Override
    public double getAsDouble() {
        return getAsExpression().getAsDouble();
    }

    @Override
    public Expression getAsExpression() {
        controller.begin();
        int total = count.getAsInt();
        for (int i = 0; i < total; i++) {
            body.getAsExpression();
            if (controller.interrupt().isBreakOrReturn()) {
                break;
            }
        }
        return controller.end();
    }

    @Override
    public FlowController controller() {
        return controller;
    }

    public Expression count() {
        return count;
    }

    public Expression body() {
        return body;
    }
}
