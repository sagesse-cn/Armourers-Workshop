package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.core.ast.ArrayAccess;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Binary;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Call;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Compound;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Literal;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Return;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Statement;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.StructAccess;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Ternary;
import moe.plushie.armourers_workshop.core.skin.molang.core.ast.Unary;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.function.Function;

/**
 * An {@link Expression} visitor. Provides a way to add
 * functionalities to the expression interface and all
 * of its implementations.
 *
 * <p>See the following example on visiting an expression:</p>
 * <pre>{@code
 *      Expression expr = ...;
 *      String str = expr.visit(new ToStringVisitor());
 * }</pre>
 */
public interface Visitor {

    /**
     * Evaluate for the given unknown expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visit(final Expression expression) {
        return expression;
    }

    /**
     * Evaluate for string expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visitLiteral(final Literal expression) {
        return visit(expression);
    }

    /**
     * Evaluate for struct access expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visitStructAccess(final StructAccess expression) {
        return visit(expression);
    }

    /**
     * Evaluate for array access expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visitArrayAccess(final ArrayAccess expression) {
        return visit(expression);
    }

    /**
     * Evaluate for call expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visitCall(final Call expression) {
        return visit(expression);
    }

    /**
     * Evaluate for unary expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visitUnary(final Unary expression) {
        return visit(expression);
    }

    /**
     * Evaluate for binary expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visitBinary(final Binary expression) {
        return visit(expression);
    }

    /**
     * Evaluate for ternary conditional expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visitTernary(final Ternary expression) {
        return visit(expression);
    }

    /**
     * Evaluate for compound expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visitCompound(final Compound expression) {
        return visit(expression);
    }

    /**
     * Evaluate for statement expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visitStatement(final Statement expression) {
        return visit(expression);
    }

    /**
     * Evaluate for return expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visitReturn(final Return expression) {
        return visit(expression);
    }

    /**
     * Evaluate for function expression.
     *
     * @param expression The expression.
     * @return The result.
     */
    default Expression visitFunction(final Function expression) {
        return visit(expression);
    }
}
