package jscl.math.generic.expression.literal;

import jscl.util.MutableInt;
import org.jetbrains.annotations.NotNull;

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

        Productand lp = LiteralUtils.getNext(l, li);
        Productand rp = LiteralUtils.getNext(r, ri);

        while (lp != null || rp != null) {
            final int c = LiteralUtils.compare(lp, rp);

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

                result.addProductand(lp.getVariable(), lp.getExponent() + rp.getExponent());

                lp = LiteralUtils.getNext(l, li);
                rp = LiteralUtils.getNext(r, ri);
            }
        }

        return result.build();
    }
}
