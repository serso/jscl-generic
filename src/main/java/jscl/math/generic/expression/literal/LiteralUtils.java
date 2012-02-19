package jscl.math.generic.expression.literal;

import jscl.math.generic.expression.Productand;
import jscl.util.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 12:24 AM
 */
class LiteralUtils {

	@Nullable
	static Productand getNext(@NotNull Literal l,
							  @NotNull MutableInt i) {
		int iInt = i.intValue();
		final Productand result = iInt < l.getSize() ? l.getProductand(iInt) : null;
		i.increment();
		return result;
	}

	static int compare(@Nullable Productand lp, @Nullable Productand rp) {
		int c;
		if (lp == null) {
			c = 1;
		} else if (rp == null) {
			c = -1;
		} else {
			c = lp.getVariable().compareTo(rp.getVariable());
		}
		return c;
	}

	@Nullable
	public static Productand getPrev(@NotNull Literal l, @NotNull MutableInt i) {
		int iInt = i.intValue();
		final Productand result = iInt > 0 ? l.getProductand(iInt) : null;
		i.decrement();
		return result;
	}
}
