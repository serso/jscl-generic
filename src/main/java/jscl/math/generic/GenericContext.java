package jscl.math.generic;

import jscl.NumeralBase;
import jscl.NumeralBaseException;
import jscl.math.generic.expression.Expression;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * User: serso
 * Date: 3/3/12
 * Time: 4:53 PM
 */
public interface GenericContext {

    @NotNull
    String format(@NotNull BigInteger intValue) throws NumeralBaseException;

    @NotNull
    String format(@NotNull BigInteger intValue, @NotNull NumeralBase numeralBase) throws NumeralBaseException;

    @NotNull
    Expression newEmptyExpression();
    //new Expression(Collections.<Summand>emptyList(), context)

    @NotNull
    GenericInteger getZero();

    @NotNull
    GenericInteger getOne();

    @NotNull
    GenericInteger newInteger(long value);
    //public static final GenericInteger ZERO = new GenericInteger(BigInteger.valueOf(0L), context);
    //public static final GenericInteger ONE = new GenericInteger(BigInteger.valueOf(1L), context);


    @NotNull
    GenericInteger newInteger(@NotNull BigInteger value);
}
