package jscl.math.generic.expression;

import jscl.math.generic.GenericInteger;
import jscl.math.generic.expression.literal.Literal;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/18/12
 * Time: 10:56 PM
 */
class Summand {

    @NotNull
    private final Literal literal;

    @NotNull
    private final GenericInteger coefficient;

    private Summand(@NotNull GenericInteger coefficient,
                    @NotNull Literal literal) {
        this.coefficient = coefficient;
        this.literal = literal;
    }

    @NotNull
    static Summand newInstance(@NotNull GenericInteger coefficient,
                               @NotNull Literal literal) {
        return new Summand(coefficient, literal);
    }

    @NotNull
    static Summand newInstance(@NotNull GenericInteger coefficient) {
        return new Summand(coefficient, Literal.newEmpty());
    }

    @NotNull
    public Literal getLiteral() {
        return literal;
    }

    @NotNull
    public GenericInteger getCoefficient() {
        return coefficient;
    }

    @NotNull
    public Summand multiply(@NotNull Summand that) {
        return newInstance(this.coefficient.multiply(that.coefficient), this.literal.multiply(that.literal));
    }
}
