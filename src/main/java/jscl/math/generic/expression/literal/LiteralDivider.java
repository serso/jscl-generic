package jscl.math.generic.expression.literal;

import jscl.math.NotDivisibleException;
import jscl.util.MutableInt;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 12:35 AM
 */
enum LiteralDivider {

    instance;

    @NotNull
    public Literal divide(@NotNull Literal l, @NotNull Literal r) throws NotDivisibleException {
        final Literal.Builder result = new Literal.Builder(l.getSize() + r.getSize());

        final MutableInt li = new MutableInt();
        final MutableInt ri = new MutableInt();

        Productand lp = LiteralUtils.getNext(l, li);
        Productand rp = LiteralUtils.getNext(r, ri);

        while (lp != null || rp != null) {
            final int c = LiteralUtils.compare(lp, rp);

            if (c < 0) {
                // dividend has more elements than divisor => just add
                assert lp != null;

                result.addProductand(lp);

                lp = LiteralUtils.getNext(l, li);
            } else if (c > 0) {
                // divisor has some element left => cannot divide
                throw new NotDivisibleException();
            } else {
                assert rp != null;
                assert lp != null;

                int s = lp.getExponent() - rp.getExponent();
                if (s < 0) {
                    throw new NotDivisibleException();
                } else if (s == 0) {
                    // no need to add productand as it equals to 1
                } else {
                    result.addProductand(lp);
                }

                lp = LiteralUtils.getNext(l, li);
                rp = LiteralUtils.getNext(r, ri);
            }
        }

        return result.build();
    }

}
