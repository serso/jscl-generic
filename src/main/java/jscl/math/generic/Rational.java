package jscl.math.generic;

import jscl.math.NotDivisibleException;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.function.Fraction;
import jscl.math.function.Inverse;
import jscl.math.generic.expression.Expression;
import jscl.mathml.MathML;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

public final class Rational extends Generic implements Numeral{

    @NotNull
    private final BigInteger n;

    @NotNull
    private final BigInteger d;

    /*
     *
     * ************************************************
     * 					CONSTRUCTORS
     * ************************************************
     *
     * */

    private Rational(@NotNull BigInteger numerator,
                     @NotNull BigInteger denominator) {
        this.n = numerator;
        this.d = denominator;
    }

    @NotNull
    public static Rational newInstance(@NotNull BigInteger numerator,
                                       @NotNull BigInteger denominator) {
        return new Rational(numerator, denominator);
    }

    @NotNull
    public static Rational newInstance(@NotNull BigInteger numerator) {
        return new Rational(numerator, BigInteger.ONE);
    }

    @NotNull
    public static Rational newInstance(long numerator,
                                       long denominator) {
        return new Rational(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    /*
     *
     * ************************************************
     * 					GETTERS
     * ************************************************
     *
     * */

    @NotNull
    public BigInteger getNumerator() {
        return n;
    }

    @NotNull
    public BigInteger getDenominator() {
        return d;
    }

    /*
     *
     * ************************************************
     * 					ADDITION
     * ************************************************
     *
     * */

    @NotNull
    public Rational add(@NotNull Rational that) {
        // n1/d1 + n2/d2
        final BigInteger gcd = this.d.gcd(that.d);

        // gcd * t1 = d1, gcd * t2 = d2
        final BigInteger t1 = this.d.divide(gcd);
        final BigInteger t2 = that.d.divide(gcd);

        // t2 * gcd * t1 = t2 * d1
        // t1 * gcd * t2 = t1 * d2
        // => ( n1 * t2 ) / (t2 * d1) + ( n2 * t1 ) / (t1 * d2) = (n1 * t2 + n2 * t1) / (d1 * t2)

        return new Rational(n.multiply(t2).add(that.n.multiply(t1)), d.multiply(t2)).reduce();
    }

    @NotNull
    public Rational add(@NotNull GenericInteger that) {
        return add(valueOf(that));
    }

    @NotNull
    public Generic add(@NotNull Generic that) {
        if (that instanceof Rational) {
            return add((Rational) that);
        } else if (that instanceof GenericInteger) {
            return add((GenericInteger) that);
        } else {
            return that.valueOf(this).add(that);
        }
    }

    /*
     *
     * ************************************************
     * 					MULTIPLICATION
     * ************************************************
     *
     * */

    @NotNull
    public Rational multiply(@NotNull Rational that) {
        // n1/d1 * n2/d2

        final BigInteger gcd1 = n.gcd(that.d);
        final BigInteger gcd2 = d.gcd(that.n);

        // n1 * n2 / gcd1 * gcd2
        final BigInteger newN = n.divide(gcd1).multiply(that.n.divide(gcd2));
        // d1 * d2 / gcd1 * gcd2
        final BigInteger newD = d.divide(gcd2).multiply(that.d.divide(gcd1));

        return new Rational(newN, newD);
    }

    @NotNull
    public Rational multiply(@NotNull GenericInteger that) {
        return multiply(valueOf(that));
    }

    @NotNull
    public Generic multiply(@NotNull Generic that) {
        if (that instanceof Rational) {
            return multiply((Rational) that);
        } else if (that instanceof GenericInteger) {
            return multiply((GenericInteger) that);
        } else {
            return that.multiply(this);
        }
    }

    /*
         *
         * ************************************************
         * 					DIVISION
         * ************************************************
         *
         * */

    @NotNull
    public Rational divide(@NotNull Rational that) {
        return multiply(that.inverse());
    }

    @NotNull
    public Rational divide(@NotNull GenericInteger that) {
        return divide(valueOf(that));
    }

    @NotNull
    public Generic divide(@NotNull Generic that) throws NotDivisibleException {
        if (that instanceof Rational) {
            return divide(that);
        } else if (that instanceof GenericInteger) {
            return divide((GenericInteger) that);
        } else {
            return that.valueOf(this).divide(that);
        }
    }

    @NotNull
    private Rational reduce() {
        BigInteger gcd = n.gcd(d);
        if (gcd.signum() != d.signum()) {
            gcd = gcd.negate();
        }

        return gcd.signum() == 0 ? this : new Rational(n.divide(gcd), d.divide(gcd));
    }

    @NotNull
    public Rational inverse() {
        if (signum() < 0) {
            return new Rational(d.negate(), n.negate());
        } else {
            return new Rational(d, n);
        }
    }

    @NotNull
    public Rational gcd(@NotNull Rational that) {
        return new Rational(n.gcd(that.n), lcm(d, that.d));
    }

    @NotNull
    public Rational gcd(@NotNull GenericInteger that) {
        return gcd(valueOf(that));
    }

    @NotNull
    public Generic gcd(@NotNull Generic that) {
        if (that instanceof Rational) {
            return gcd((Rational) that);
        } else if (that instanceof GenericInteger) {
            return gcd((GenericInteger) that);
        } else {
            return that.valueOf(this).gcd(that);
        }
    }

    @NotNull
    private static BigInteger lcm(@NotNull BigInteger i1, @NotNull BigInteger i2) {
        return i1.multiply(i2).divide(i1.gcd(i2));
    }

    @NotNull
    public Generic gcd() {
        return null;
    }

    @NotNull
    public Generic pow(int exponent) {
        return new Rational(n.pow(exponent), d.pow(exponent));
    }

    @NotNull
    public Generic negate() {
        return new Rational(n.negate(), d);
    }

    public int signum() {
        return n.signum();
    }

    public int degree() {
        return 0;
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

    public Generic expand() {
        return this;
    }

    public Generic factorize() {
        return expressionValue().factorize();
    }

    public Generic elementary() {
        return this;
    }

    public Generic simplify() {
        return reduce();
    }

    public Generic numeric() {
        return new NumericWrapper(this);
    }

    @NotNull
    public Rational valueOf(@NotNull GenericInteger that) {
        return newInstance(that.getContent());
    }

    @NotNull
    public Rational valueOf(@NotNull Generic that) {
        if (that instanceof Rational) {
            return (Rational) that;
        } else if (that instanceof Expression) {
            /*boolean sign = that.signum() < 0;
               Generic g[] = ((Fraction) (sign ? that.negate() : that).variableValue()).getParameters();
               GenericInteger numerator = (GenericInteger) (sign ? g[0].negate() : g[0]);
               GenericInteger denominator = (GenericInteger) g[1];
               return new Rational(numerator.getContent(), denominator.getContent());
               */// todo serso: implement
        } else if (that instanceof GenericInteger) {
            return valueOf((GenericInteger) that);
        }
    }

    public Generic[] sumValue() {
        try {
            if (integerValue().signum() == 0) return new Generic[0];
            else return new Generic[]{this};
        } catch (NotIntegerException e) {
            return new Generic[]{this};
        }
    }

    public Generic[] productValue() throws NotProductException {
        try {
            if (integerValue().compareTo(GenericInteger.valueOf(1)) == 0) return new Generic[0];
            else return new Generic[]{this};
        } catch (NotIntegerException e) {
            return new Generic[]{this};
        }
    }

    public Power powerValue() throws NotPowerException {
        return new Power(this, 1);
    }

    public Expression expressionValue() throws NotExpressionException {
        return Expression.valueOf(this);
    }

    public GenericInteger integerValue() throws NotIntegerException {
        if (d.compareTo(BigInteger.ONE) == 0) {
            return new GenericInteger(n);
        } else {
            throw new NotIntegerException();
        }
    }

    @Override
    public boolean isInteger() {
        try {
            integerValue();
            return true;
        } catch (NotIntegerException e) {
            return false;
        }
    }

    public Variable variableValue() throws NotVariableException {
        try {
            integerValue();
            throw new NotVariableException();
        } catch (NotIntegerException e) {
            if (n.compareTo(BigInteger.valueOf(1)) == 0) return new Inverse(new GenericInteger(d));
            else return new Fraction(new GenericInteger(n), new GenericInteger(d));
        }
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

    public int compareTo(Rational rational) {
        int c = d.compareTo(rational.d);
        if (c < 0) return -1;
        else if (c > 0) return 1;
        else return n.compareTo(rational.n);
    }

    public int compareTo(Generic generic) {
        if (generic instanceof Rational) {
            return compareTo((Rational) generic);
        } else if (generic instanceof GenericInteger) {
            return compareTo(valueOf(generic));
        } else {
            return generic.valueOf(this).compareTo(generic);
        }
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();
        try {
            result.append(integerValue());
        } catch (NotIntegerException e) {
            result.append(n);
            result.append("/");
            result.append(d);
        }
        return result.toString();
    }

    public String toJava() {
        return "JsclDouble.valueOf(" + n + "/" + d + ")";
    }

    public void toMathML(MathML element, Object data) {
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
        try {
            MathML e1 = element.element("mn");
            e1.appendChild(element.text(String.valueOf(integerValue())));
            element.appendChild(e1);
        } catch (NotIntegerException e) {
            MathML e1 = element.element("mfrac");
            MathML e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(n)));
            e1.appendChild(e2);
            e2 = element.element("mn");
            e2.appendChild(element.text(String.valueOf(d)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }
}
