package jscl.math.generic.expression;

import jscl.util.MutableInt;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

import static jscl.math.generic.expression.ExpressionUtils.getPrev;

/**
 * User: serso
 * Date: 3/3/12
 * Time: 4:18 PM
 */
public enum ExpressionComparator implements Comparator<Expression>{
    
    instance;

    public int compare(@NotNull Expression l, @NotNull Expression r) {
        final MutableInt li = new MutableInt(l.getSize() - 1);
        final MutableInt ri = new MutableInt(r.getSize() - 1);

        Summand ls = getPrev(l, li);
        Summand rs = getPrev(r, ri);

        while (ls != null || rs != null) {
            int c;

            if (ls == null) {
                c = -1;
            } else if (rs == null) {
                c = 1;
            } else {
                c = ls.getLiteral().compareTo(rs.getLiteral());
            }

            if (c < 0) {
                return -1;
            } else if (c > 0) {
                return 1;
            } else {
                assert rs != null;
                assert ls != null;

                c = ls.getCoefficient().compareTo(rs.getCoefficient());
                if (c < 0) {
                    return -1;
                } else if (c > 0) {
                    return 1;
                }
                
                ls = getPrev(l, li);
                rs = getPrev(r, ri);
            }
        }
        return 0;
    }
}
