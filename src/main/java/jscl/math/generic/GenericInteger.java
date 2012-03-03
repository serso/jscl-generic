package jscl.math.generic;

import jscl.math.NotDivisibleException;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.mathml.MathML;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class GenericInteger extends Generic implements Numeral {

    @NotNull
    private final BigInteger content;

    /*
    **********************************************************************
    *
    *                           CONSTRUCTORS
    *
    **********************************************************************
    */

    private GenericInteger(@NotNull BigInteger content, @NotNull GenericContext context) {
        super(context);
        this.content = content;
    }

    @NotNull
    public static GenericInteger newInstance(long value, @NotNull GenericContext context) {
        if (value == 0L) {
            return new GenericInteger(BigInteger.ZERO, context);
        } else if (value == 1L) {
            return new GenericInteger(BigInteger.ONE, context);
        } else {
            return new GenericInteger(BigInteger.valueOf(value), context);
        }
    }

    @NotNull
    public static GenericInteger newInstance(@NotNull BigInteger value, @NotNull GenericContext context) {
        return new GenericInteger(value, context);
    }

    /*
    **********************************************************************
    *
    *                           GETTERS
    *
    **********************************************************************
    */


    @NotNull
    public BigInteger getContent() {
        return content;
    }
    
    /*
    **********************************************************************
    *
    *                           OTHER
    *
    **********************************************************************
    */

    public boolean isZero() {
        return this.content.compareTo(BigInteger.ZERO) == 0;
    }

    public boolean isOne() {
        return this.content.compareTo(BigInteger.ONE) == 0;
    }
    
    /*
    **********************************************************************
    *
    *                           ADDITION
    *
    **********************************************************************
    */

    @NotNull
    public GenericInteger add(@NotNull GenericInteger that) {
        return new GenericInteger(content.add(that.content), context);
    }

    @NotNull
    public Generic add(@NotNull Generic that) {
        if (that instanceof GenericInteger) {
            return add((GenericInteger) that);
        } else {
            return that.valueOf(this).add(that);
        }
    }
    
    /*
    **********************************************************************
    *
    *                           SUBTRACTION
    *
    **********************************************************************
    */

    public GenericInteger subtract(GenericInteger integer) {
        return new GenericInteger(content.subtract(integer.content), context);
    }

    @NotNull
    public Generic subtract(@NotNull Generic that) {
        if (that instanceof GenericInteger) {
            return subtract((GenericInteger) that);
        } else {
            return that.valueOf(this).subtract(that);
        }
    }
    
    /*
    **********************************************************************
    *
    *                           MULTIPLICATION
    *
    **********************************************************************
    */

    public GenericInteger multiply(GenericInteger integer) {
        return new GenericInteger(content.multiply(integer.content), context);
    }

    @NotNull
    public Generic multiply(@NotNull Generic that) {
        if (that instanceof GenericInteger) {
            return multiply((GenericInteger) that);
        } else {
            return that.multiply(this);
        }
    }
    
    /*
    **********************************************************************
    *
    *                           DIVISION
    *
    **********************************************************************
    */

    public GenericInteger divide(@NotNull GenericInteger that) {
        final DivideAndRemainderResult<GenericInteger> dadr = divideAndRemainder(that);
        if (dadr.getRemainder().isZero()) {
            return dadr.getDivisionResult();
        } else {
            throw new NotDivisibleException();
        }
    }

    @NotNull
    public Generic divide(@NotNull Generic that) throws NotDivisibleException {
        if (that instanceof GenericInteger) {
            return divide((GenericInteger) that);
        } else {
            return that.valueOf(this).divide(that);
        }
    }

    @NotNull
    private DivideAndRemainderResult<GenericInteger> divideAndRemainder(@NotNull GenericInteger that) {
        try {
            final BigInteger result[] = content.divideAndRemainder(that.content);
            return DivideAndRemainderResult.newInstance(new GenericInteger(result[0], context), new GenericInteger(result[1], context));
        } catch (ArithmeticException e) {
            throw new NotDivisibleException();
        }
    }

    @NotNull
    public DivideAndRemainderResult divideAndRemainder(@NotNull Generic that) {
        if (that instanceof GenericInteger) {
            return divideAndRemainder((GenericInteger) that);
        } else {
            return that.valueOf(this).divideAndRemainder(that);
        }
    }

    public GenericInteger remainder(GenericInteger integer) throws ArithmeticException {
        return new GenericInteger(content.remainder(integer.content), context);
    }

    @NotNull
    public Generic getRemainder(@NotNull Generic that) throws ArithmeticException {
        if (that instanceof GenericInteger) {
            return remainder((GenericInteger) that);
        } else {
            return that.valueOf(this).getRemainder(that);
        }
    }
    
    /*
    **********************************************************************
    *
    *                           GCD
    *
    **********************************************************************
    */

    @NotNull
    public GenericInteger gcd(@NotNull GenericInteger integer) {
        return new GenericInteger(content.gcd(integer.content), context);
    }

    @NotNull
    public Generic gcd(@NotNull Generic that) {
        if (that instanceof GenericInteger) {
            return gcd((GenericInteger) that);
        } else {
            return that.valueOf(this).gcd(that);
        }
    }

    @NotNull
    public GenericInteger getIntegerGcd() {
        return new GenericInteger(BigInteger.valueOf(signum()), context);
    }

    /*
    **********************************************************************
    *
    *                           OTHER OPERATIONS
    *
    **********************************************************************
    */
    
    @NotNull
    public Generic pow(int exponent) {
        return new GenericInteger(content.pow(exponent), context);
    }

    @NotNull
    public GenericInteger negate() {
        return new GenericInteger(content.negate(), context);
    }

    public int signum() {
        return content.signum();
    }

    public int degree() {
        return 0;
    }

    public GenericInteger mod(GenericInteger integer) {
        return new GenericInteger(content.mod(integer.content), context);
    }

    public GenericInteger modPow(GenericInteger exponent, GenericInteger integer) {
        return new GenericInteger(content.modPow(exponent.content, integer.content), context);
    }

    public GenericInteger modInverse(GenericInteger integer) {
        return new GenericInteger(content.modInverse(integer.content), context);
    }

/*    public GenericInteger phi() {
        if (signum() == 0) return this;
        Generic a = factorize();
        Generic p[] = a.productValue();
        Generic s = GenericInteger.newInstance(1);
        for (int i = 0; i < p.length; i++) {
            Power o = p[i].powerValue();
            Generic q = o.value(true);
            int c = o.exponent();
            s = s.multiply(q.subtract(GenericInteger.newInstance(1)).multiply(q.pow(c - 1)));
        }
        return s.integerValue();
    }

    public GenericInteger[] primitiveRoots() {
        GenericInteger phi = phi();
        Generic a = phi.factorize();
        Generic p[] = a.productValue();
        GenericInteger d[] = new GenericInteger[p.length];
        for (int i = 0; i < p.length; i++) {
            d[i] = phi.divide(p[i].powerValue().value(true).integerValue());
        }
        int k = 0;
        GenericInteger n = this;
        GenericInteger m = GenericInteger.newInstance(1);
        GenericInteger r[] = new GenericInteger[phi.phi().intValue()];
        while (m.compareTo(n) < 0) {
            boolean b = m.gcd(n).compareTo(GenericInteger.newInstance(1)) == 0;
            for (int i = 0; i < d.length; i++) {
                b = b && m.modPow(d[i], n).compareTo(GenericInteger.newInstance(1)) > 0;
            }
            if (b) r[k++] = m;
            m = m.add(GenericInteger.newInstance(1));
        }
        return k > 0 ? r : new GenericInteger[0];
    }*/

    public GenericInteger sqrt() {
        return nthrt(2);
    }

    public GenericInteger nthrt(int n) {
//      return JsclInteger.valueOf((int)Math.pow((double)intValue(),1./n));
        if (signum() == 0) {
            return GenericInteger.newInstance(0, this.context);
        } else if (signum() < 0) {
            if (n % 2 == 0) {
                throw new ArithmeticException("Could not calculate root of negative argument: " + this + " of odd order: " + n);
            } else {
                return negate().nthrt(n).negate();
            }
        } else {
            Generic x0;
            Generic x = this;
            do {
                x0 = x;
                x = divideAndRemainder(x.pow(n - 1)).getDivisionResult().add(x.multiply(GenericInteger.newInstance(n - 1, this.context))).divideAndRemainder(GenericInteger.newInstance(n, this.context)).getDivisionResult();
            } while (x.compareTo(x0) < 0);
            return x0.integerValue();
        }
    }

    @NotNull
    public Generic antiDerivative(@NotNull Variable variable) throws NotIntegrableException {
        return multiply(variable.asGeneric());
    }

    @NotNull
    public Generic derivative(@NotNull Variable variable) {
        return GenericInteger.newInstance(0, this.context);
    }

    @NotNull
    public Generic substitute(@NotNull Variable variable, Generic generic) {
        return this;
    }

    @Override
    public Generic newInstance(@NotNull Generic generic) {
        throw new ArithmeticException();
    }

    @NotNull
    public Generic expand() {
        return this;
    }

    @NotNull
    public Generic factorize() {
        throw new UnsupportedOperationException();
        //return Factorization.compute(this);
    }

    @NotNull
    public Generic elementary() {
        return this;
    }

    @NotNull
    public Generic simplify() {
        return this;
    }

    @NotNull
    public Generic numeric() {
        return GenericNumeric.newInstance(this);
    }

    @NotNull
    public Generic valueOf(@NotNull Generic generic) {
        return new GenericInteger(((GenericInteger) generic).content, context);
    }

    @NotNull
    public List<GenericInteger> sumValue() {
        if ( isZero() ) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(this);
        }
    }

    @NotNull
    public List<GenericInteger> productValue() throws NotProductException {
        if (isOne()) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(this);
        }
    }

/*    public Power powerValue() throws NotPowerException {
        if (content.signum() < 0) throw new NotPowerException();
        else return new Power(this, 1);
    }

    public Expression expressionValue() throws NotExpressionException {
        return Expression.valueOf(this);
    }*/

    @NotNull
    public GenericInteger integerValue() throws NotIntegerException {
        return this;
    }

/*    @Override
    public boolean isInteger() {
        return true;
    }*/

    @NotNull
    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
    }

    @NotNull
    public List<Variable> variables() {
        return Collections.emptyList();
    }

    public boolean isPolynomial(@NotNull Variable variable) {
        return true;
    }

    public boolean isConstant(@NotNull Variable variable) {
        return true;
    }

    public int intValue() {
        return content.intValue();
    }

    public int compareTo(@NotNull GenericInteger that) {
        return content.compareTo(that.content);
    }

    public int compareTo(@NotNull Generic that) {
        if (that instanceof GenericInteger) {
            return compareTo((GenericInteger) that);
        } else {
            return that.valueOf(this).compareTo(that);
        }
    }

    public String toString() {
        return context.format(this.content);
    }

    public String toJava() {
        return "JsclDouble.valueOf(" + content + ")";
    }

    public void toMathML(MathML element, @Nullable Object data) {
        int exponent = data instanceof Integer ? (Integer) data : 1;
        if (exponent == 1) bodyToMathML(element);
        else {
            MathML e1 = element.newElement("msup");
            bodyToMathML(e1);
            MathML e2 = element.newElement("mn");
            e2.appendChild(element.newText(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

/*    @NotNull
    @Override
    public Set<? extends Constant> getConstants() {
        return Collections.emptySet();
    }*/

    void bodyToMathML(MathML element) {
        MathML e1 = element.newElement("mn");
        e1.appendChild(element.newText(String.valueOf(content)));
        element.appendChild(e1);
    }
}
