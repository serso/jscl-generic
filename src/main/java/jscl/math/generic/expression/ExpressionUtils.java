package jscl.math.generic.expression;

import jscl.util.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 3/3/12
 * Time: 4:24 PM
 */
class ExpressionUtils {

    private ExpressionUtils() {
        throw new AssertionError();
    }

    @Nullable
    static Summand getNext(@NotNull Expression e,
                              @NotNull MutableInt i) {
        int iInt = i.intValue();
        final Summand result = iInt < e.getSize() ? e.getSummand(iInt) : null;
        i.increment();
        return result;
    }


    @Nullable
    public static Summand getPrev(@NotNull Expression e, @NotNull MutableInt i) {
        int iInt = i.intValue();
        final Summand result = iInt > 0 ? e.getSummand(iInt) : null;
        i.decrement();
        return result;
    }
}
