package moe.plushie.armourers_workshop.core.skin.animation.molang.runtime;

import moe.plushie.armourers_workshop.core.skin.animation.molang.ast.Binary;
import moe.plushie.armourers_workshop.core.skin.animation.molang.ast.Ternary;
import moe.plushie.armourers_workshop.core.skin.animation.molang.ast.Unary;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Expression;

public class PrettyPrinter {

    public static String toString(Unary expr) {
        return expr.op().symbol() + escape(expr, expr.value());
    }

    public static String toString(Binary expr) {
        return escape(expr, expr.left()) + " " + expr.op().symbol() + " " + escape(expr, expr.right());
    }

    public static String toString(Ternary expr) {
        return escape(expr, expr.condition()) + " ? " + escape(expr, expr.trueValue()) + " : " + escape(expr, expr.falseValue());
    }

    public static String escape(Expression self, Expression expression) {
        // multiple operands have different precedence.
        if (expression instanceof Binary || expression instanceof Ternary) {
            return "(" + expression + ")";
        }
        return expression.toString();
    }

}
