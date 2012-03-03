package jscl.math.generic.expression.literal;


import jscl.math.Variable;
import jscl.math.generic.Generic;
import jscl.math.generic.NotVariableException;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/19/12
 * Time: 12:27 AM
 */
public class Productand {

    @NotNull
    private final Variable variable;

    private final int exponent;

    private Productand(@NotNull Variable variable, int exponent) {
        this.variable = variable;
        this.exponent = exponent;
    }

    @NotNull
    static Productand newInstance(@NotNull Variable variable, int exponent) {
        return new Productand(variable, exponent);
    }

    @NotNull
    static Productand newInstance(@NotNull Variable variable) {
        return new Productand(variable, 1);
    }

    @NotNull
    public Variable getVariable() {
        return variable;
    }

    public int getExponent() {
        return exponent;
    }

    @NotNull
    public Variable variableValue() {
        if ( exponent == 1 ) {
            return variable;
        } else {
            throw new NotVariableException();
        }
    }

    @NotNull
    public Generic asGeneric() {
        if (exponent == 0) {
            return variable.getContext().getOne();
        } else {
            return variable.asGeneric().pow(exponent);
        }
    }
}
