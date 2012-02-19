package jscl.math.generic;

import jscl.math.Arithmetic;
import jscl.math.NotDivisibleException;
import jscl.math.generic.expression.Expression;
import jscl.mathml.MathML;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Generic implements Arithmetic<Generic>, Comparable {

	@NotNull
	public Generic subtract(@NotNull Generic that) {
		return add(that.negate());
	}

	public boolean multiple(Generic generic) throws ArithmeticException {
		return remainder(generic).signum() == 0;
	}

/*    public Arithmetic add(@NotNull Arithmetic arithmetic) {
        return add((Generic)arithmetic);
    }

    public Arithmetic subtract(@NotNull Arithmetic arithmetic) {
        return subtract((Generic)arithmetic);
    }

    public Arithmetic multiply(@NotNull Arithmetic arithmetic) {
        return multiply((Generic)arithmetic);
    }

    public Generic divide(@NotNull Arithmetic arithmetic) throws ArithmeticException {
        return divide((Generic)arithmetic);
    }*/

	@NotNull
	public DivideAndRemainderResult divideAndRemainder(@NotNull Generic generic) {
		try {
			return new Generic[]{divide(generic), JsclInteger.valueOf(0)};
		} catch (NotDivisibleException e) {
			return new Generic[]{JsclInteger.valueOf(0), this};
		}
	}

	public Generic remainder(Generic generic) throws ArithmeticException {
		return divideAndRemainder(generic)[1];
	}

	public Generic inverse() {
		return JsclInteger.valueOf(1).divide(this);
	}

	public abstract Generic gcd(@NotNull Generic generic);

	public Generic scm(Generic generic) {
		return divide(gcd(generic)).multiply(generic);
	}

	@NotNull
	protected abstract Generic gcd();

	public Generic[] gcdAndNormalize() {
		Generic gcd = gcd();

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

	public Generic pow(int exponent) {
		assert exponent >= 0;

		Generic result = JsclInteger.valueOf(1);

		for (int i = 0; i < exponent; i++) {

			//ParserUtils.checkInterruption();

			result = result.multiply(this);
		}

		return result;
	}

	public Generic abs() {
		return signum() < 0 ? negate() : this;
	}

	public abstract Generic negate();

	public abstract int signum();

	public abstract int degree();

	//    public abstract Generic mod(Generic generic);
//    public abstract Generic modPow(Generic exponent, Generic generic);
//    public abstract Generic modInverse(Generic generic);
//    public abstract boolean isProbablePrime(int certainty);
	public abstract Generic antiDerivative(@NotNull Variable variable) throws NotIntegrableException;

	public abstract Generic derivative(@NotNull Variable variable);

	public abstract Generic substitute(@NotNull Variable variable, Generic generic);

	public abstract Generic expand();

	public abstract Generic factorize();

	public abstract Generic elementary();

	public abstract Generic simplify();

	public abstract Generic numeric();

	public abstract Generic valueOf(Generic generic);

	public abstract Generic[] sumValue();

	public abstract Generic[] productValue() throws NotProductException;

	public abstract Power powerValue() throws NotPowerException;

	public abstract Expression expressionValue() throws NotExpressionException;

	public abstract boolean isInteger();

	public abstract JsclInteger integerValue() throws NotIntegerException;

	public abstract Variable variableValue() throws NotVariableException;

	public abstract Variable[] variables();

	public abstract boolean isPolynomial(@NotNull Variable variable);

	public abstract boolean isConstant(@NotNull Variable variable);

	public boolean isIdentity(@NotNull Variable variable) {
		try {
			return variableValue().isIdentity(variable);
		} catch (NotVariableException e) {
			return false;
		}
	}

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