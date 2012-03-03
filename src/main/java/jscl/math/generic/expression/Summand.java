package jscl.math.generic.expression;

import jscl.math.Variable;
import jscl.math.generic.Generic;
import jscl.math.generic.GenericInteger;
import jscl.math.generic.NotIntegerException;
import jscl.math.generic.NotVariableException;
import jscl.math.generic.expression.literal.Literal;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    @NotNull
    public GenericInteger integerValue() {
        GenericInteger result;

        if (this.literal.getDegree() == 0) {
            result = this.coefficient;
        } else {
            throw new NotIntegerException();
        }

        return result;
    }

    @NotNull
    public Variable variableValue() {
        if (coefficient.isOne()) {
            return literal.variableValue();
        } else {
            throw new NotVariableException();
        }
    }

    @NotNull
    public List<Generic> productValue() {
        final List<Generic> result = literal.productValue();
        if ( coefficient.isOne() ) {
            return result;
        } else {
            // must multiply result on coefficient => just add another productand
            final List<Generic> tmp = new ArrayList<Generic>(result.size() + 1);
            tmp.add(coefficient);
            tmp.addAll(result);
            return tmp;
        }
    }

    public boolean isZero() {
        if ( coefficient.isZero() ) {
            return true;
        } else {
            return false;
            // todo serso: can the literal be zero? (Can the variable be zero?)
            //return literal.isZero();
        }
    }
}
