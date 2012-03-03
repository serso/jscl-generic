package jscl.math.generic;

import jscl.math.*;
import jscl.mathml.MathML;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Generic implements Arithmetic<Generic>, Comparable, Transformable, IGeneric<Generic> {

    @NotNull
    protected final GenericContext context;

    protected Generic(@NotNull GenericContext context) {
        this.context = context;
    }

    @NotNull
    public GenericContext getContext() {
        return context;
    }

    @NotNull
    public Generic subtract(@NotNull Generic that) {
        return add(that.negate());
    }

    public boolean isMultiple(@NotNull Generic that) throws ArithmeticException {
        return getRemainder(that).isZero();
    }

    @NotNull
    public DivisionResult divideAndRemainder(@NotNull Generic generic) {
        try {
            return DivisionResult.newInstance(divide(generic), context.getZero());
        } catch (NotDivisibleException e) {
            return DivisionResult.newInstance(context.getZero(), this);
        }
    }

    @NotNull
    public Generic getRemainder(@NotNull Generic that) throws ArithmeticException {
        return divideAndRemainder(that).getRemainder();
    }

    @NotNull
    @Override
    public Generic inverse() {
        return context.getOne().divide(this);
    }

    @Override
    @NotNull
    public Generic scm(@NotNull Generic that) {
        final Generic gcd = this.gcd(that);
        return this.divide(gcd).multiply(that);
    }

    /**
     * @return integer value to be the greatest common divisor of this object
     */
    @NotNull
    protected abstract GenericInteger getIntegerGcd();

    public Generic[] gcdAndNormalize() {
        GenericInteger gcd = getIntegerGcd();

        if (gcd.signum() == 0) {
            return new Generic[]{gcd, this};
        }

        if (gcd.signum() != signum()) {
            gcd = gcd.negate();
        }

        return new Generic[]{gcd, divide(gcd)};
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public Generic normalize() {
        return gcdAndNormalize()[1];
    }

    @NotNull
    @Override
    public Generic pow(int exponent) {
        if ( exponent < 0 ) {
            throw new IllegalArgumentException("Exponent must be positive");
        }

        Generic result = context.getOne();

        for (int i = 0; i < exponent; i++) {

            //ParserUtils.checkInterruption();

            result = result.multiply(this);
        }

        return result;
    }

    @NotNull
    @Override
    public Generic abs() {
        return signum() < 0 ? negate() : this;
    }

    //    public abstract Generic mod(Generic generic);
//    public abstract Generic modPow(Generic exponent, Generic generic);
//    public abstract Generic modInverse(Generic generic);
//    public abstract boolean isProbablePrime(int certainty);
    public abstract Generic antiDerivative(@NotNull Variable variable) throws NotIntegrableException;

    public abstract Generic derivative(@NotNull Variable variable);

    public abstract Generic substitute(@NotNull Variable variable, Generic generic);

    @NotNull
    public abstract List<? extends Generic> sumValue();

    @NotNull
	public abstract List<? extends Generic> productValue() throws NotProductException;

/*	public abstract Power powerValue() throws NotPowerException;

	public abstract Expression expressionValue() throws NotExpressionException;

	public abstract boolean isInteger();*/

    public abstract GenericInteger integerValue() throws NotIntegerException;

	public abstract Variable variableValue() throws NotVariableException;

    @NotNull
    public abstract List<Variable> variables();

    public abstract boolean isPolynomial(@NotNull Variable variable);

    public abstract boolean isConstant(@NotNull Variable variable);

/*	public boolean isIdentity(@NotNull Variable variable) {
		try {
			return variableValue().isIdentity(variable);
		} catch (NotVariableException e) {
			return false;
		}
	}*/

    public abstract int compareTo(Generic generic);

    public int compareTo(Object o) {
        return compareTo((Generic) o);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof Generic) {
            final Generic that = ((Generic) o);
            return compareTo(that) == 0;
        }

        return false;
    }

    public abstract String toJava();

    public String toMathML() {
        final MathML document = new MathML("math", "-//W3C//DTD MathML 2.0//EN", "http://www.w3.org/TR/MathML2/dtd/mathml2.dtd");

        final MathML e = document.newElement("math");
        toMathML(e, null);
        return e.toString();
    }

    public abstract void toMathML(MathML element, @Nullable Object data);

    //@NotNull
    //public abstract Set<? extends Constant> getConstants();

    public abstract boolean isZero();
}
