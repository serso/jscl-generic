package jscl.math.generic.expression;

import jscl.util.MutableInt;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * User: serso
 * Date: 2/19/12
 * Time: 12:48 AM
 */
enum LiteralMultiplicator {

	instance;

	@NotNull
	public Literal multiply(@NotNull Literal l, @NotNull Literal r) {
		final Literal.Builder result = new Literal.Builder(l.getSize() + r.getSize());

		final MutableInt li = new MutableInt();
		final MutableInt ri = new MutableInt();

		final List<Productand> lProductands = l.getProductands();
		final List<Productand> rProductands = r.getProductands();

		Productand lp = getNext(l, li, lProductands);
		Productand rp = getNext(r, ri, rProductands);

		while (lp != null || rp != null) {
			final int c;
			
			if (lp == null) {
				c = 1;
			} else if (rp == null) {
				c = -1;
			} else {
				c = lp.compareTo(rp);
			}

			if (c < 0) {
				assert lp != null;

				result.addProductand(lp);
				lp = getNext(l, li, lProductands);
			} else if (c > 0) {
				assert rp != null;

				result.addProductand(rp);
				rp = getNext(r, ri, rProductands);
			} else {
				assert lp != null;
				assert rp != null;

				result.addProductand(lp.getVariable(), lp.getExponent() + rp.getExponent());

				lp = getNext(l, li, lProductands);
				rp = getNext(r, ri, rProductands);
			}
		}

		return result.build();
	}

	private Productand getNext(@NotNull Literal l, 
							   @NotNull MutableInt i, 
							   @NotNull List<Productand> productands) {
		int intValue = i.intValue();
		final Productand result = intValue < l.getSize() ? productands.get(intValue) : null;
		i.increment();
		return result;
	}
}
