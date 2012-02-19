package jscl.math.generic.expression.literal;

import jscl.util.MutableInt;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 12:21 AM
 */
enum LiteralGcd {
	
	instance;

	@NotNull
	public Literal gcd(@NotNull Literal l, @NotNull Literal r) {
		final Literal.Builder result = new Literal.Builder(Math.min(l.getSize(), r.getSize()));
		
		final MutableInt li = new MutableInt();
		final MutableInt ri = new MutableInt();
		
		Productand lp = LiteralUtils.getNext(l, li);
		Productand rp = LiteralUtils.getNext(r, ri);

		while (lp != null || rp != null) {
			final int c = LiteralUtils.compare(lp, rp);

			if (c < 0) {
				lp = LiteralUtils.getNext(l, li);
			} else if (c > 0) {
				rp = LiteralUtils.getNext(r, ri);
			} else {
				assert lp != null;
				assert rp != null;
				
				int minExponent = Math.min(lp.getExponent(), rp.getExponent());

				result.addProductand(lp.getVariable(), minExponent);

				lp = LiteralUtils.getNext(l, li);
				rp = LiteralUtils.getNext(r, ri);
			}
		}

		return result.build();
	}
}
