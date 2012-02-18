package jscl.math.generic.expression;


import jscl.math.generic.Variable;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/19/12
 * Time: 12:27 AM
 */
public class Productand implements Comparable<Productand>{

	@NotNull
	private final Variable variable;

	private final int exponent;

	private Productand(@NotNull Variable variable, int exponent) {
		this.variable = variable;
		this.exponent = exponent;
	}

	@NotNull
	public static Productand newInstance(@NotNull Variable variable, int exponent) {
		return new Productand(variable, exponent);
	}

	@NotNull
	public static Productand newInstance(@NotNull Variable variable) {
		return new Productand(variable, 1);
	}

	@NotNull
	public Variable getVariable() {
		return variable;
	}

	public int getExponent() {
		return exponent;
	}

	public int compareTo(@NotNull Productand that) {
		return this.variable.compareTo(that.variable);
	}
}
