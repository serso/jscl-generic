package jscl.math.generic;

import jscl.JsclMathContextImpl;
import jscl.math.NotDivisibleException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * User: serso
 * Date: 3/3/12
 * Time: 10:15 PM
 */
public class GenericIntegerTest {

    private static final GenericContext context = new GenericContextImpl(JsclMathContextImpl.defaultInstance());    
    
    @Test
    public void testIsZero() throws Exception {
        Assert.assertTrue(context.newInteger(0).isZero());
        Assert.assertFalse(context.newInteger(1).isZero());
        Assert.assertTrue(context.newInteger(new BigInteger("100000000000000").subtract(new BigInteger("100000000000000"))).isZero());
        Assert.assertFalse(context.newInteger(new BigInteger("100000000000001").subtract(new BigInteger("100000000000000"))).isZero());
    }

    @Test
    public void testIsOne() throws Exception {
        Assert.assertFalse(context.newInteger(0).isOne());
        Assert.assertTrue(context.newInteger(1).isOne());
        Assert.assertFalse(context.newInteger(new BigInteger("100000000000000").subtract(new BigInteger("100000000000000"))).isOne());
        Assert.assertTrue(context.newInteger(new BigInteger("100000000000001").subtract(new BigInteger("100000000000000"))).isOne());
    }

    @Test
    public void testAdd() throws Exception {
        testAddition(1, 2, 3);
        testAddition(5, 2, 7);
        testAddition(5000000000L, 2, 5000000002L);
        testAddition(5000000004L, -2, 5000000002L);
        testAddition(-4, -2, -6);
        testAddition(-4, 0, -4);
        testAddition(74, 0, 74);
    }

    private void testAddition(long l, long r, long expected) {
        Assert.assertEquals(context.newInteger(expected), context.newInteger(l).add(context.newInteger(r)));
    }

    @Test
    public void testSubtract() throws Exception {
         testSubtraction(1, 2, -1);
         testSubtraction(10, 2, 8);
         testSubtraction(10, -2, 12);
         testSubtraction(-10, -2, -8);
         testSubtraction(-10, -0, -10);
    }

    private void testSubtraction(long l, long r, long expected) {
        Assert.assertEquals(context.newInteger(expected), context.newInteger(l).subtract(context.newInteger(r)));
    }

    @Test
    public void testMultiply() throws Exception {
       testMultiplication(2, 3, 2*3);
       testMultiplication(3000000L, 3000000L, 3000000L*3000000L);
       testMultiplication(3000000L, 0L, 0L);
       testMultiplication(3000000L, -1L, -3000000L);
       testMultiplication(-20L, -14L, 20 * 14);
    }

    private void testMultiplication(long l, long r, long expected) {
        Assert.assertEquals(context.newInteger(expected), context.newInteger(l).multiply(context.newInteger(r)));
    }

    @Test
    public void testDivide() throws Exception {
        testDivision(4, 2, 2);
        testDivision(42, 2, 21);
        testDivision(-42, 2, -21);
        testDivision(-425, 5, -425/5);
        testDivision(-425, -5, 425/5);
        testDivision(500, 50, 10);
        testDivision(20, 1, 20);
        testDivision(20, 20, 1);
        testDivision(0, 2, 0);

        try {
            testDivision(500, 0, 10);
            Assert.fail();
        } catch (ArithmeticException e) {
            // ok
        }

        try {
            testDivision(0, -0, 10);
            Assert.fail();
        } catch (ArithmeticException e) {
            // ok
        }

        try {
            testDivision(1, 2, 10);
            Assert.fail();
        } catch (NotDivisibleException e) {
            // ok
        }

        try {
            testDivision(-2, 3, 10);
            Assert.fail();
        } catch (NotDivisibleException e) {
            // ok
        }

    }

    private void testDivision(long l, long r, long expected) {
        Assert.assertEquals(context.newInteger(expected), context.newInteger(l).divide(context.newInteger(r)));
    }


    @Test
    public void testDivideAndRemainder() throws Exception {
        testDivideAndRemainder(300, 300, 1, 0);
        testDivideAndRemainder(300, 301, 0, 300);
        testDivideAndRemainder(301, 300, 1, 1);
        testDivideAndRemainder(301, -300, -1, 1);
        testDivideAndRemainder(-301, 300, -1, -1);
        testDivideAndRemainder(-42, -5, 8, -2);
        testDivideAndRemainder(-42, 9, -4, -6);

        try {
            testDivideAndRemainder(-42, 0, -4, -6);
            Assert.fail();
        } catch (Exception e) {
            // ok
        }
    }

    private void testDivideAndRemainder(long l, long r, long expectedQuotient, long expectedRemainder) {
        DivisionResult darr = context.newInteger(l).divideAndRemainder(context.newInteger(r));
        Assert.assertEquals(context.newInteger(expectedQuotient), darr.getQuotient());
        Assert.assertEquals(context.newInteger(expectedRemainder), darr.getRemainder());
    }

    @Test
    public void testGcd() throws Exception {
        testGcd(5, 7, 1);
        testGcd(5, 15, 5);
        testGcd(15, 3, 3);
        testGcd(15, 10, 5);
        testGcd(15, 1, 1);
        testGcd(15, 0, 15);
        testGcd(-15, 0, 15);
        testGcd(0, 10, 10);
        testGcd(0, -10, 10);
        testGcd(15, -5, 5);
        testGcd(-15, -5, 5);
        testGcd(-15, 5, 5);
        testGcd(-15, 34342342352L, 1);
        testGcd(4, 34342342352L, 4);
        testGcd(0, 0, 0);

    }

    private void testGcd(long l, long r, long expected) {
        Assert.assertEquals(context.newInteger(expected), context.newInteger(l).gcd(context.newInteger(r)));
    }

    @Test
    public void testPow() throws Exception {
        testPow(10, 2, 100);
        testPow(10, 0, 1);
        testPow(0, 1, 0);
        testPow(134, 1, 134);
        try {
            testPow(10, -2, 1);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    private void testPow(long l, int r, long expected) {
        Assert.assertEquals(context.newInteger(expected), context.newInteger(l).pow(r));
    }

    @Test
    public void testNegate() throws Exception {
        testNegate(1, -1);
        testNegate(-1, 1);
        testNegate(0, 0);
        testNegate(10000000, -10000000);
    }

    private void testNegate(int l, int expected) {
        Assert.assertEquals(context.newInteger(expected), context.newInteger(l).negate());
    }

    @Test
    public void testSignum() throws Exception {

    }

    @Test
    public void testSqrt() throws Exception {

    }

    @Test
    public void testNthrt() throws Exception {

    }

    @Test
    public void testAntiDerivative() throws Exception {

    }

    @Test
    public void testDerivative() throws Exception {

    }

    @Test
    public void testSubstitute() throws Exception {

    }

    @Test
    public void testNewInstance() throws Exception {

    }

    @Test
    public void testExpand() throws Exception {

    }

    @Test
    public void testFactorize() throws Exception {

    }

    @Test
    public void testElementary() throws Exception {

    }

    @Test
    public void testSimplify() throws Exception {

    }

    @Test
    public void testNumeric() throws Exception {

    }

    @Test
    public void testValueOf() throws Exception {

    }

    @Test
    public void testSumValue() throws Exception {

    }

    @Test
    public void testProductValue() throws Exception {

    }

    @Test
    public void testIntegerValue() throws Exception {

    }

    @Test
    public void testVariableValue() throws Exception {

    }

    @Test
    public void testVariables() throws Exception {

    }

    @Test
    public void testIsPolynomial() throws Exception {

    }

    @Test
    public void testIsConstant() throws Exception {

    }

    @Test
    public void testIntValue() throws Exception {

    }

    @Test
    public void testCompareTo() throws Exception {

    }

    @Test
    public void testToString() throws Exception {

    }

    @Test
    public void testToJava() throws Exception {

    }

    @Test
    public void testToMathML() throws Exception {

    }
}
