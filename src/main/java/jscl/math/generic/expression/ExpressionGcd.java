package jscl.math.generic.expression;

import jscl.math.Variable;
import jscl.math.generic.Generic;
import jscl.math.generic.expression.literal.Literal;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * User: serso
 * Date: 3/3/12
 * Time: 3:00 PM
 */
public enum ExpressionGcd {
    instance;

    public Generic gcd(@NotNull Expression l, @NotNull Expression r) {
        final Literal lLcm = l.literalLcm();
        final Literal rLcm = r.literalLcm();

        final Literal gcd = lLcm.gcd(rLcm);

        final List<Variable> vars = gcd.getVariables();
        if (vars.size() == 0) {
            if (l.signum() == 0) {
                return r;
            } else {
                return l.gcd(r.getIntegerGcd());
            }
        } else {
            /*Polynomial p = Polynomial.factory(vars[0]);
            return p.valueOf(this).gcd(p.valueOf(r)).genericValue();*/
            throw new UnsupportedOperationException();
        }

    }
}
