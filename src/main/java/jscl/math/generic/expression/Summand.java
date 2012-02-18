package jscl.math.generic.expression;

import jscl.math.generic.GenericInteger;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/18/12
 * Time: 10:56 PM
 */
public class Summand implements Comparable<Summand> {

	@NotNull
	private final Literal literal;

	@NotNull
	private final GenericInteger coefficient;

	private Summand(@NotNull GenericInteger coefficient,
					@NotNull Literal literal) {
		this.coefficient = coefficient;
		this.literal = literal;
	}

	@NotNull
	public static Summand newInstance(@NotNull GenericInteger coefficient,
									  @NotNull Literal literal) {
		return new Summand(coefficient, literal);
	}

	public int compareTo(@NotNull Summand that) {
		return this.literal.compareTo(that.literal);
	}

	@NotNull
	public Literal getLiteral() {
		return literal;
	}

	@NotNull
	public GenericInteger getCoefficient() {
		return coefficient;
	}

	@NotNull
	public Summand multiply ( @NotNull Summand that ) {
		return newInstance(this.coefficient.multiply(that.coefficient), this.literal.multiply(that.literal));
	}
}
