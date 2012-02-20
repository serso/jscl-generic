package jscl.math.generic.expression.literal;

import jscl.util.MutableInt;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 12:53 AM
 */
enum LiteralLcm {

	instance;

	@NotNull
	public Literal lcm(@NotNull Literal l, @NotNull Literal r) {
		final Literal.Builder result = new Literal.Builder(l.getSize() + r.getSize());

		MutableInt li = new MutableInt();
		MutableInt ri = new MutableInt();

		Productand lp = LiteralUtils.getNext(l, li);
		Productand rp = LiteralUtils.getNext(r, ri);

		while (lp != null || rp != null) {
			int c = LiteralUtils.compare(lp, rp);

			if (c < 0) {
				assert lp != null;

				result.addProductand(lp);
				lp = LiteralUtils.getNext(l, li);
			} else if (c > 0) {
				assert rp != null;

				result.addProductand(rp);
				rp = LiteralUtils.getNext(r, ri);
			} else {
				assert lp != null;
				assert rp != null;

				int maxExponent = Math.max(lp.getExponent(), rp.getExponent());

				result.addProductand(lp.getVariable(), maxExponent);

				lp = LiteralUtils.getNext(l, li);
				rp = LiteralUtils.getNext(r, ri);
			}
		}

		return result.build();
	}
}
