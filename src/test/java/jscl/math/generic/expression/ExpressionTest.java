package jscl.math.generic.expression;

import jscl.JsclMathContextImpl;
import jscl.math.generic.GenericContext;
import jscl.math.generic.GenericContextImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 3/3/12
 * Time: 6:16 PM
 */
public class ExpressionTest {

    private static final GenericContext context = new GenericContextImpl(JsclMathContextImpl.defaultInstance());
    
    @Test
    public void testAdd() throws Exception {
        final Expression one = Expression.newInstance(context.newInteger(1));

        Assert.assertEquals("1.0", one.toString());
        Assert.assertEquals("2.0", one.add(one).toString());
    }
}
