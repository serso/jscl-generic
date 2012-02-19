package jscl.math.generic.expression;

import jscl.math.generic.GenericInteger;
import jscl.util.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * User: serso
 * Date: 2/18/12
 * Time: 11:21 PM
 */
enum ExpressionSummizer {

	instance;

	@NotNull
	public Expression sum(@NotNull Expression l, @NotNull Expression r) {
		return this.sum(l, null, r, null);
	}

	public Expression sum(@NotNull Expression l, @Nullable Summand lMultiplier, @NotNull Expression r, @Nullable Summand rMultiplier) {
		final Expression.Builder result = new Expression.Builder(l.getSize() + r.getSize());

		final MutableInt li = new MutableInt(l.getSize());
		final MutableInt ri = new MutableInt(r.getSize());

		final List<Summand> lSummands = l.getSummands();
		final List<Summand> rSummands = r.getSummands();

		Summand ls = getNext(lSummands, li, lMultiplier);
		Summand rs = getNext(rSummands, ri, rMultiplier);

		while (ls != null || rs != null) {
			int c;

			if (ls == null) {
				c = 1;
			} else if (rs == null) {
				c = -1;
			} else {
				c = -ls.getLiteral().compareTo(rs.getLiteral());
			}

			if (c < 0) {
				assert ls != null;

				result.addSummand(ls);
				ls = getNext(lSummands, li, lMultiplier);
			} else if (c > 0) {
				assert rs != null;

				result.addSummand(rs);
				rs = getNext(rSummands, ri, rMultiplier);
			} else {
				assert rs != null;
				assert ls != null;

				final GenericInteger coefficient = ls.getCoefficient().add(rs.getCoefficient());
				if (!coefficient.isZero()) {
					// ls == rs => set any
					result.addSummand(coefficient, ls.getLiteral());
				}

				ls = getNext(lSummands, li, lMultiplier);
				rs = getNext(rSummands, ri, rMultiplier);
			}
		}

		return result.build();
	}

	@Nullable
	private Summand getNext(@NotNull List<Summand> list, @NotNull MutableInt i, @Nullable Summand multiplier) {
		int intValue = i.intValue();
		if (intValue > 0) {
			final Summand result = list.get(intValue);
			i.decrement();
			if (multiplier != null) {
				return result.multiply(multiplier);
			} else {
				return result;
			}
		} else {
			return null;
		}
	}
}
