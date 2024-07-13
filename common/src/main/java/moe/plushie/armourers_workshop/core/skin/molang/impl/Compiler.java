package moe.plushie.armourers_workshop.core.skin.molang.impl;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.core.skin.molang.core.Binary;
import moe.plushie.armourers_workshop.core.skin.molang.core.BooleanNegate;
import moe.plushie.armourers_workshop.core.skin.molang.core.Break;
import moe.plushie.armourers_workshop.core.skin.molang.core.Compound;
import moe.plushie.armourers_workshop.core.skin.molang.core.Constant;
import moe.plushie.armourers_workshop.core.skin.molang.core.Continue;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;
import moe.plushie.armourers_workshop.core.skin.molang.core.Identifier;
import moe.plushie.armourers_workshop.core.skin.molang.core.Literal;
import moe.plushie.armourers_workshop.core.skin.molang.core.Negative;
import moe.plushie.armourers_workshop.core.skin.molang.core.Return;
import moe.plushie.armourers_workshop.core.skin.molang.core.Subscript;
import moe.plushie.armourers_workshop.core.skin.molang.core.Ternary;
import moe.plushie.armourers_workshop.core.skin.molang.core.Variable;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.ACos;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.ASin;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.ATan;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.ATan2;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.Abs;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.Cos;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.Exp;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.Log;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.Mod;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.Pow;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.Sin;
import moe.plushie.armourers_workshop.core.skin.molang.function.generic.Sqrt;
import moe.plushie.armourers_workshop.core.skin.molang.function.limit.Clamp;
import moe.plushie.armourers_workshop.core.skin.molang.function.limit.Max;
import moe.plushie.armourers_workshop.core.skin.molang.function.limit.Min;
import moe.plushie.armourers_workshop.core.skin.molang.function.misc.Pi;
import moe.plushie.armourers_workshop.core.skin.molang.function.misc.ToDeg;
import moe.plushie.armourers_workshop.core.skin.molang.function.misc.ToRad;
import moe.plushie.armourers_workshop.core.skin.molang.function.random.DieRoll;
import moe.plushie.armourers_workshop.core.skin.molang.function.random.DieRollInteger;
import moe.plushie.armourers_workshop.core.skin.molang.function.random.Random;
import moe.plushie.armourers_workshop.core.skin.molang.function.random.RandomInteger;
import moe.plushie.armourers_workshop.core.skin.molang.function.round.Ceil;
import moe.plushie.armourers_workshop.core.skin.molang.function.round.Floor;
import moe.plushie.armourers_workshop.core.skin.molang.function.round.HermiteBlend;
import moe.plushie.armourers_workshop.core.skin.molang.function.round.Lerp;
import moe.plushie.armourers_workshop.core.skin.molang.function.round.LerpRot;
import moe.plushie.armourers_workshop.core.skin.molang.function.round.Round;
import moe.plushie.armourers_workshop.core.skin.molang.function.round.Truncate;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for parsing and utilising MoLang functions and expressions
 *
 * @see <a href="https://bedrock.dev/docs/1.19.0.0/1.19.30.23/Molang#Math%20Functions">Bedrock Dev - Molang</a>
 */
public class Compiler {

    protected final Map<KeyPath, Variable> variables = new ConcurrentHashMap<>();
    protected final Map<KeyPath, Function.Factory<?>> functions = new ConcurrentHashMap<>();

    private final Map<String, KeyPath> mapping = new ConcurrentHashMap<>();
    private final Map<String, String> aliases = ImmutableMap.<String, String>builder()
            .put("c", "context")
            .put("q", "query")
            .put("t", "temp")
            .put("v", "variable")
            .build();

    public Compiler() {
        // Some default values
        registerVariable("PI", new Variable("PI", Math.PI));
        registerVariable("E", new Variable("E", Math.E));

        // Rounding functions
        registerFunction("math.floor", Floor::new);
        registerFunction("math.round", Round::new);
        registerFunction("math.ceil", Ceil::new);
        registerFunction("math.trunc", Truncate::new);

        // Selection and limit functions
        registerFunction("math.clamp", Clamp::new);
        registerFunction("math.max", Max::new);
        registerFunction("math.min", Min::new);

        // Classical functions
        registerFunction("math.abs", Abs::new);
        registerFunction("math.acos", ACos::new);
        registerFunction("math.asin", ASin::new);
        registerFunction("math.atan", ATan::new);
        registerFunction("math.atan2", ATan2::new);
        registerFunction("math.cos", Cos::new);
        registerFunction("math.sin", Sin::new);
        registerFunction("math.exp", Exp::new);
        registerFunction("math.ln", Log::new);
        registerFunction("math.sqrt", Sqrt::new);
        registerFunction("math.mod", Mod::new);
        registerFunction("math.pow", Pow::new);

        // Utility functions
        registerFunction("math.lerp", Lerp::new);
        registerFunction("math.lerprotate", LerpRot::new);
        registerFunction("math.hermite_blend", HermiteBlend::new);
        registerFunction("math.die_roll", DieRoll::new);
        registerFunction("math.die_roll_integer", DieRollInteger::new);
        registerFunction("math.random", Random::new);
        registerFunction("math.random_integer", RandomInteger::new);

        registerFunction("math.pi", Pi::new);
        registerFunction("math.to_deg", ToDeg::new);
        registerFunction("math.to_rad", ToRad::new);
    }

    /**
     * Register a new {@link Function} to be handled by GeckoLib for parsing and internal use.
     * <p>
     * Overrides are supported, but should be avoided unless specifically needed
     *
     * @param name    The string representation of the function. This will be the parsed value from input math strings.
     * @param factory The constructor-factory for the given function
     */
    public void registerFunction(String name, Function.Factory<?> factory) {
        functions.put(KeyPath.of(name), factory);
    }

    /**
     * Register a new {@link Variable} with the math parsing system
     * <p>
     * Technically supports overriding by matching keys, though you should try to update the existing variable instances instead if possible
     */
    public void registerVariable(String name, Variable variable) {
        var key = resolveName(name);
        variables.put(key, variable);
    }

    /**
     * @return The registered {@link Variable} instance for the given (aliased) name.
     */
    public Variable getVariable(String name) {
        var key = resolveName(name);
        return variables.computeIfAbsent(key, it -> new Variable(it.toString(), 0));
    }

    /**
     * @return The registered {@link Variable} functions for the given (aliased) name.
     */
    public Function.Factory<?> getFunction(String name) {
        return functions.get(KeyPath.of(name));
    }

    /**
     * A wrapper around the expression parsing system to optionally support Molang-specific handling for things like compound expressions
     *
     * @param source The math and/or Molang expression to be parsed
     * @return A compiled {@link Expression}, ready for use
     */
    public Expression compile(String source) throws SyntaxException {
        var lexer = new Lexer(source);
        var expressions = new ArrayList<Expression>();
        while (true) {
            var token = lexer.next();
            if (token.kind() == Lexer.TokenKind.EOF) {
                // reached end-of-file!
                break;
            }
            if (token.kind() == Lexer.TokenKind.ERROR) {
                // tokenization error!
                throw new SyntaxException("Found an invalid token (error): " + token.value(), lexer.cursor());
            }

            var expr = parseCompoundExpression(lexer, 0);

            // check current token, should be a semicolon or an eof
            token = lexer.current();
            if (token.kind() != Lexer.TokenKind.EOF && token.kind() != Lexer.TokenKind.SEMICOLON) {
                throw new SyntaxException("Expected a semicolon, but was " + token, lexer.cursor());
            }
            expressions.add(expr);
        }

        // only once, not need wrapper.
        if (expressions.size() == 1) {
            return expressions.get(0);
        }

        return new Compound(expressions);
    }

    /**
     * Get an {@link KeyPath} for a given aliased name.
     */
    protected KeyPath resolveName(String name) {
        var key = mapping.get(name);
        if (key != null) {
            return key;
        }
        key = KeyPath.parse(name);
        var resolvedName = aliases.get(key.getName());
        if (resolvedName != null) {
            key = new KeyPath(resolvedName, key.getChild());
        }
        mapping.put(name, key);
        return key;
    }

    /**
     * Parses a single expression.
     * Single expressions don't require a left-hand expression
     * to be parsed, e.g. literals, statements, identifiers,
     * wrapped expressions and execution scopes
     */
    private Expression parseSingle(Lexer lexer) throws SyntaxException {
        var token = lexer.current();
        return switch (token.kind()) {
            case NUMBER -> {
                lexer.next();
                yield new Constant(Double.parseDouble(token.value()));
            }
            case STRING -> {
                lexer.next();
                yield new Literal(token.value());
            }
            case TRUE -> {
                lexer.next();
                yield Constant.ONE;
            }
            case FALSE -> {
                lexer.next();
                yield Constant.ZERO;
            }
            case LPAREN -> {
                lexer.next();
                // wrapped expression: (expression)
                var expression = parseCompoundExpression(lexer, 0);
                token = lexer.current();
                if (token.kind() != Lexer.TokenKind.RPAREN) {
                    throw new SyntaxException("Non closed expression", lexer.cursor());
                }
                lexer.next();
                yield expression;
            }
            case LBRACE -> {
                lexer.next();
                var expressions = new ArrayList<Expression>();
                while (true) {
                    expressions.add(parseCompoundExpression(lexer, 0));
                    token = lexer.current();
                    if (token.kind() == Lexer.TokenKind.RBRACE) {
                        lexer.next();
                        break;
                    }
                    if (token.kind() == Lexer.TokenKind.EOF) {
                        // end reached but not closed yet, huh?
                        throw new SyntaxException("Found the end before the execution scope closing token", lexer.cursor());
                    }
                    if (token.kind() == Lexer.TokenKind.ERROR) {
                        throw new SyntaxException("Found an invalid token (error): " + token.value(), lexer.cursor());
                    }
                    if (token.kind() != Lexer.TokenKind.SEMICOLON) {
                        throw new SyntaxException("Missing semicolon", lexer.cursor());
                    }
                    lexer.next();
                }
                yield new Compound(expressions);
            }
            case BREAK -> {
                lexer.next();
                yield new Break();
            }
            case CONTINUE -> {
                lexer.next();
                yield new Continue();
            }
            case IDENTIFIER -> {
                var name = token.value();
                token = lexer.next();
                while (token.kind() == Lexer.TokenKind.DOT) {
                    token = lexer.next();
                    if (token.kind() != Lexer.TokenKind.IDENTIFIER) {
                        throw new SyntaxException("Unexpected token, expected a valid field token", lexer.cursor());
                    }
                    name = name + "." + token.value();
                    token = lexer.next();
                }
                // determine type by the next token.
                if (token.kind() != Lexer.TokenKind.LPAREN) {
                    yield getVariable(name);
                }
                yield new Identifier(name);
            }
            case SUB -> {
                lexer.next();
                var expr = parseSingle(lexer);
                yield new Negative(expr);
            }
            case BANG -> {
                lexer.next();
                var expr = parseSingle(lexer);
                yield new BooleanNegate(expr);
            }
            case RETURN -> {
                lexer.next();
                var expr = parseCompoundExpression(lexer, 0);
                yield new Return(expr);
            }
            default -> Constant.ZERO;
        };
    }

    private Expression parseCompoundExpression(Lexer lexer, int lastPrecedence) throws SyntaxException {
        var expr = parseSingle(lexer);
        while (true) {
            final var compoundExpr = parseCompound(lexer, expr, lastPrecedence);
            final var current = lexer.current();
            if (current.kind() == Lexer.TokenKind.EOF || current.kind() == Lexer.TokenKind.SEMICOLON) {
                // found eof, stop parsing, return expr
                return compoundExpr;
            }
            if (compoundExpr == expr) {
                return expr;
            }
            expr = compoundExpr;
        }
    }

    private Expression parseCompound(Lexer lexer, Expression left, int lastPrecedence) throws SyntaxException {
        var current = lexer.current();
        switch (current.kind()) {
            case RPAREN:
            case EOF: {
                return left;
            }
            case LBRACKET: { // ARRAY ACCESS EXPRESSION: "left["
                current = lexer.next();
                if (current.kind() == Lexer.TokenKind.RBRACKET) {
                    throw new SyntaxException("Expected a expression, got RBRACKET", lexer.cursor());
                }
                if (current.kind() == Lexer.TokenKind.EOF) {
                    throw new SyntaxException("Found EOF before closing RBRACKET", lexer.cursor());
                }
                final var index = parseCompoundExpression(lexer, 0);
                current = lexer.current();
                if (current.kind() == Lexer.TokenKind.EOF) {
                    throw new SyntaxException("Found EOF before closing RBRACKET", lexer.cursor());
                }
                if (current.kind() != Lexer.TokenKind.RBRACKET) {
                    throw new SyntaxException("Expected a closing RBRACKET, found " + current, lexer.cursor());
                }
                lexer.next();
                return new Subscript(left, index);
            }
            case LPAREN: { // CALL EXPRESSION: "left("
                current = lexer.next();
                final var arguments = new ArrayList<Expression>();

                // start reading the arguments
                if (current.kind() == Lexer.TokenKind.EOF) {
                    throw new SyntaxException("Found EOF before closing RPAREN", lexer.cursor());
                }

                // find all arguments.
                while (current.kind() != Lexer.TokenKind.RPAREN) {
                    arguments.add(parseCompoundExpression(lexer, 0));
                    // update current character
                    current = lexer.current();
                    if (current.kind() == Lexer.TokenKind.EOF) {
                        throw new SyntaxException("Found EOF before closing RPAREN", lexer.cursor());
                    }
                    if (current.kind() == Lexer.TokenKind.ERROR) {
                        throw new SyntaxException("Found error token: " + current.value(), lexer.cursor());
                    }
                    if (current.kind() == Lexer.TokenKind.RPAREN) {
                        break;
                    }
                    if (current.kind() != Lexer.TokenKind.COMMA) {
                        throw new SyntaxException("Expected a comma, got " + current.kind(), lexer.cursor());
                    }
                    lexer.next();
                }
                lexer.next();

                var factory = getFunction(left.toString());
                if (factory == null) {
                    throw new SyntaxException("Found an invalid function, got " + current.kind(), lexer.cursor());
                }
                return factory.create(arguments);
            }
            case QUES: {
                lexer.next();
                var trueValue = parseCompoundExpression(lexer, 0);
                if (lexer.current().kind() == Lexer.TokenKind.COLON) {
                    // then it's a ternary expression, since there is a ':', indicating the next expression
                    lexer.next();
                    var falseValue = parseCompoundExpression(lexer, 0);
                    return new Ternary(left, trueValue, falseValue);
                }
                return new Binary(Binary.Op.CONDITIONAL, left, trueValue);
            }
        }

        // check for binary expressions
        final var op = switch (current.kind()) {
            case AMPAMP -> Binary.Op.AND;
            case BARBAR -> Binary.Op.OR;
            case LT -> Binary.Op.LT;
            case LTE -> Binary.Op.LTE;
            case GT -> Binary.Op.GT;
            case GTE -> Binary.Op.GTE;
            case PLUS -> Binary.Op.ADD;
            case SUB -> Binary.Op.SUB;
            case STAR -> Binary.Op.MUL;
            case SLASH -> Binary.Op.DIV;
            case QUESQUES -> Binary.Op.NULL_COALESCE;
            case ASSIGN -> Binary.Op.ASSIGN;
            case EQ -> Binary.Op.EQ;
            case BANGEQ -> Binary.Op.NEQ;
            case ARROW -> Binary.Op.ARROW;
            default -> null;
        };

        if (op == null || lastPrecedence >= op.precedence()) {
            return left;
        }

        lexer.next();
        var right = parseCompoundExpression(lexer, op.precedence());
        return new Binary(op, left, right);
    }
}

