package jscl.math.generic;

import jscl.JsclMathContext;
import jscl.NumeralBase;
import jscl.NumeralBaseException;
import jscl.math.generic.expression.Expression;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * User: serso
 * Date: 3/3/12
 * Time: 6:06 PM
 */
public class GenericContextImpl implements GenericContext {

    @NotNull
    private final JsclMathContext mathContext;

    public GenericContextImpl(@NotNull JsclMathContext mathContext) {
        this.mathContext = mathContext;
    }

    @NotNull
    @Override
    public String format(@NotNull BigInteger intValue) throws NumeralBaseException {
        return this.mathContext.format(this.mathContext.fromLong(intValue.longValue()));
    }

    @NotNull
    @Override
    public String format(@NotNull BigInteger intValue, @NotNull NumeralBase numeralBase) throws NumeralBaseException {
        return this.mathContext.format(this.mathContext.fromLong(intValue.longValue()), numeralBase);
    }

    @NotNull
    @Override
    public Expression newEmptyExpression() {
        return Expression.newEmpty(this);
    }

    @NotNull
    @Override
    public GenericInteger getZero() {
        return GenericInteger.newInstance(0L, this);
    }

    @NotNull
    @Override
    public GenericInteger getOne() {
        return GenericInteger.newInstance(1L, this);
    }

    @NotNull
    @Override
    public GenericInteger newInteger(long value) {
        return GenericInteger.newInstance(value, this);
    }

    @NotNull
    @Override
    public GenericInteger newInteger(@NotNull BigInteger value) {
        return GenericInteger.newInstance(value, this);
    }
}
