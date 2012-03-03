package jscl.math.generic.expression.literal;

import jscl.util.MutableInt;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 1:04 AM
 */
enum LiteralComparator implements Comparator<Literal> {

    instance;

    public int compare(@NotNull Literal l, @NotNull Literal r) {
        final MutableInt li = new MutableInt(l.getSize() - 1);
        final MutableInt ri = new MutableInt(r.getSize() - 1);

        Productand lp = LiteralUtils.getPrev(l, li);
        Productand rp = LiteralUtils.getPrev(r, ri);

        while (lp != null || rp != null) {
            int c = LiteralUtils.compare(lp, rp);

            if (c == 0) {

                assert lp != null;
                assert rp != null;

                if (lp.getExponent() < rp.getExponent()) {
                    return -1;
                } else if (lp.getExponent() > rp.getExponent()) {
                    return 1;
                } else {
                    lp = LiteralUtils.getPrev(l, li);
                    rp = LiteralUtils.getPrev(r, ri);
                }

            } else {
                return c;
            }

        }

        return 0;
    }
}
