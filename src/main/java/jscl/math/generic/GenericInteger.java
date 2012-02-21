package jscl.math.generic;

import jscl.JsclMathEngine;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.mathml.MathML;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

public final class GenericInteger extends Generic implements Numeral {

    public static final GenericInteger ZERO = new GenericInteger(BigInteger.valueOf(0L));
    public static final GenericInteger ONE = new GenericInteger(BigInteger.valueOf(1L));

    @NotNull
    private final BigInteger content;

    private GenericInteger(@NotNull BigInteger content) {
        this.content = content;
    }

    @NotNull
    public BigInteger getContent() {
        return content;
    }

    public boolean isZero() {
        return this.content.compareTo(BigInteger.ZERO) == 0;
    }

    @NotNull
    public static GenericInteger newInstance(long value) {
        if (value == 0L) {
            return ZERO;
        } else if (value == 1L) {
            return ONE;
        } else {
            return new GenericInteger(BigInteger.valueOf(value));
        }
    }

    public static GenericInteger valueOf(long val) {
        switch ((int) val) {
            case 0:
                return ZERO;
            case 1:
                return ONE;
            default:
                return new GenericInteger(BigInteger.valueOf(val));
        }
    }

    @NotNull
    public GenericInteger add(@NotNull GenericInteger that) {
        return new GenericInteger(content.add(that.content));
    }

    @NotNull
    public Generic add(@NotNull Generic that) {
        if (that instanceof GenericInteger) {
            return add((GenericInteger) that);
        } else {
            return that.valueOf(this).add(that);
        }
    }

    public GenericInteger subtract(GenericInteger integer) {
        return new GenericInteger(content.subtract(integer.content));
    }

    @NotNull
    public Generic subtract(@NotNull Generic that) {
        if (that instanceof GenericInteger) {
            return subtract((GenericInteger) that);
        } else {
            return that.valueOf(this).subtract(that);
        }
    }

    public GenericInteger multiply(GenericInteger integer) {
        return new GenericInteger(content.multiply(integer.content));
    }

    @NotNull
    public Generic multiply(@NotNull Generic that) {
        if (that instanceof GenericInteger) {
            return multiply((GenericInteger) that);
        } else {
            return that.multiply(this);
        }
    }

    public GenericInteger divide(@NotNull GenericInteger that) {
        GenericInteger e[] = divideAndRemainder(that);
        if (e[1].signum() == 0) {
            return e[0];
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
    private GenericInteger[] divideAndRemainder(@NotNull GenericInteger that) {
        try {
            final BigInteger result[] = content.divideAndRemainder(that.content);
            return new GenericInteger[]{new GenericInteger(result[0]), new GenericInteger(result[1])};
        } catch (ArithmeticException e) {
            throw new NotDivisibleException();
        }
    }

    @NotNull
    public Generic[] divideAndRemainder(@NotNull Generic that) {
        if (that instanceof GenericInteger) {
            return divideAndRemainder((GenericInteger) that);
        } else {
            return that.valueOf(this).divideAndRemainder(that);
        }
    }

    public GenericInteger remainder(GenericInteger integer) throws ArithmeticException {
        return new GenericInteger(content.remainder(integer.content));
    }

    @NotNull
    public Generic getRemainder(@NotNull Generic that) throws ArithmeticException {
        if (that instanceof GenericInteger) {
            return remainder((GenericInteger) that);
        } else {
            return that.valueOf(this).remainder(that);
        }
    }

    @NotNull
    public GenericInteger gcd(@NotNull GenericInteger integer) {
        return new GenericInteger(content.gcd(integer.content));
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
    public Generic gcd() {
        return new GenericInteger(BigInteger.valueOf(signum()));
    }

    @NotNull
    public Generic pow(int exponent) {
        return new GenericInteger(content.pow(exponent));
    }

    @NotNull
    public Generic negate() {
        return new GenericInteger(content.negate());
    }

    public int signum() {
        return content.signum();
    }

    public int degree() {
        return 0;
    }

    public GenericInteger mod(GenericInteger integer) {
        return new GenericInteger(content.mod(integer.content));
    }

    public GenericInteger modPow(GenericInteger exponent, GenericInteger integer) {
        return new GenericInteger(content.modPow(exponent.content, integer.content));
    }

    public GenericInteger modInverse(GenericInteger integer) {
        return new GenericInteger(content.modInverse(integer.content));
    }

    public GenericInteger phi() {
        if (signum() == 0) return this;
        Generic a = factorize();
        Generic p[] = a.productValue();
        Generic s = GenericInteger.valueOf(1);
        for (int i = 0; i < p.length; i++) {
            Power o = p[i].powerValue();
            Generic q = o.value(true);
            int c = o.exponent();
            s = s.multiply(q.subtract(GenericInteger.valueOf(1)).multiply(q.pow(c - 1)));
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
        GenericInteger m = GenericInteger.valueOf(1);
        GenericInteger r[] = new GenericInteger[phi.phi().intValue()];
        while (m.compareTo(n) < 0) {
            boolean b = m.gcd(n).compareTo(GenericInteger.valueOf(1)) == 0;
            for (int i = 0; i < d.length; i++) {
                b = b && m.modPow(d[i], n).compareTo(GenericInteger.valueOf(1)) > 0;
            }
            if (b) r[k++] = m;
            m = m.add(GenericInteger.valueOf(1));
        }
        return k > 0 ? r : new GenericInteger[0];
    }

    public GenericInteger sqrt() {
        return nthrt(2);
    }

    public GenericInteger nthrt(int n) {
//      return JsclInteger.valueOf((int)Math.pow((double)intValue(),1./n));
        if (signum() == 0) {
            return GenericInteger.valueOf(0);
        } else if (signum() < 0) {
            if (n % 2 == 0) {
                throw new ArithmeticException("Could not calculate root of negative argument: " + this + " of odd order: " + n);
            } else {
                return (GenericInteger) ((GenericInteger) negate()).nthrt(n).negate();
            }
        } else {
            Generic x0;
            Generic x = this;
            do {
                x0 = x;
                x = divideAndRemainder(x.pow(n - 1))[0].add(x.multiply(GenericInteger.valueOf(n - 1))).divideAndRemainder(GenericInteger.valueOf(n))[0];
            } while (x.compareTo(x0) < 0);
            return x0.integerValue();
        }
    }

    public Generic antiDerivative(@NotNull Variable variable) throws NotIntegrableException {
        return multiply(variable.expressionValue());
    }

    public Generic derivative(@NotNull Variable variable) {
        return GenericInteger.valueOf(0);
    }

    public Generic substitute(@NotNull Variable variable, Generic generic) {
        return this;
    }

    @NotNull
    public Generic expand() {
        return this;
    }

    @NotNull
    public Generic factorize() {
        return Factorization.compute(this);
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
        return new NumericWrapper(this);
    }

    @NotNull
    public Generic valueOf(@NotNull Generic generic) {
        return new GenericInteger(((GenericInteger) generic).content);
    }

    public Generic[] sumValue() {
        if (content.signum() == 0) return new Generic[0];
        else return new Generic[]{this};
    }

    public Generic[] productValue() throws NotProductException {
        if (content.compareTo(BigInteger.valueOf(1)) == 0) return new Generic[0];
        else return new Generic[]{this};
    }

    public Power powerValue() throws NotPowerException {
        if (content.signum() < 0) throw new NotPowerException();
        else return new Power(this, 1);
    }

    public Expression expressionValue() throws NotExpressionException {
        return Expression.valueOf(this);
    }

    public GenericInteger integerValue() throws NotIntegerException {
        return this;
    }

    @Override
    public boolean isInteger() {
        return true;
    }

    public Variable variableValue() throws NotVariableException {
        throw new NotVariableException();
    }

    @NotNull
    public Variable[] variables() {
        return new Variable[0];
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

    public int compareTo(GenericInteger integer) {
        return content.compareTo(integer.content);
    }

    public int compareTo(Generic generic) {
        if (generic instanceof GenericInteger) {
            return compareTo((GenericInteger) generic);
        } else {
            return generic.valueOf(this).compareTo(generic);
        }
    }


    public static GenericInteger valueOf(String str) {
        return new GenericInteger(new BigInteger(str));
    }

    public String toString() {
        // todo serso: actually better way is to provide custom format() method for integers and not to convert integer to double
        return JsclMathEngine.instance.format(this.content.doubleValue());
    }

    public String toJava() {
        return "JsclDouble.valueOf(" + content + ")";
    }

    public void toMathML(MathML element, @Nullable Object data) {
        int exponent = data instanceof Integer ? (Integer) data : 1;
        if (exponent == 1) bodyToMathML(element);
        else {
            MathML e1 = element.element("msup");
            bodyToMathML(e1);
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    @NotNull
    @Override
    public Set<? extends Constant> getConstants() {
        return Collections.emptySet();
    }

    void bodyToMathML(MathML element) {
        MathML e1 = element.element("mn");
        e1.appendChild(element.text(String.valueOf(content)));
        element.appendChild(e1);
    }
}
