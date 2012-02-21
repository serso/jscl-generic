package jscl.math.generic.expression;

import jscl.ImmutableObjectBuilder;
import jscl.JsclMathEngine;
import jscl.math.NotDivisibleException;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.function.Fraction;
import jscl.math.function.Inverse;
import jscl.math.generic.DivideAndRemainderResult;
import jscl.math.generic.Generic;
import jscl.math.generic.GenericInteger;
import jscl.math.generic.expression.literal.Literal;
import jscl.math.numeric.Real;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.UnivariatePolynomial;
import jscl.mathml.MathML;
import jscl.newText.*;
import jscl.newText.msg.Messages;
import jscl.util.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Expression extends Generic {

    @NotNull
    private final List<Summand> summands;

    private Expression(@NotNull List<Summand> summands) {
        this.summands = summands;
    }

    public static class Builder extends ImmutableObjectBuilder<Expression> {

        @NotNull
        private final List<Summand> summands;

        public Builder(int initialCapacity) {
            this.summands = new ArrayList<Summand>(initialCapacity);
        }

        public void addSummand(@NotNull Summand s) {
            if (isLocked()) {
                throw new IllegalStateException("Cannot add summand to already created expression!");
            }
            this.summands.add(s);
        }

        public void addSummand(@NotNull GenericInteger coefficient, @NotNull Literal literal) {
            addSummand(Summand.newInstance(coefficient, literal));
        }

        @NotNull
        public Expression build0() {
            // avoid not used memory
            if (summands instanceof ArrayList) {
                ((ArrayList) summands).trimToSize();
            }

            return new Expression(this.summands);
        }
    }

    public int getSize() {
        return this.summands.size();
    }

    @NotNull
    public List<Summand> getSummands() {
        return Collections.unmodifiableList(summands);
    }

    @NotNull
    public Expression add(@NotNull Expression that) {
        return ExpressionSummizer.instance.sum(this, that);
    }

    @NotNull
    public Generic add(@NotNull Generic that) {
        if (that instanceof Expression) {
            return add((Expression) that);
        } else if (that instanceof JsclInteger || that instanceof Rational || that instanceof NumericWrapper) {
            return add(newInstance(that));
        } else {
            return that.newInstance(this).add(that);
        }
    }

    @NotNull
    public Expression subtract(@NotNull Expression that) {
        return ExpressionSummizer.instance.sum(this, null, that, Summand.newInstance(GenericInteger.newInstance(-1L), Literal.newInstance()));
    }

    @NotNull
    public Generic subtract(@NotNull Generic that) {
        if (that instanceof Expression) {
            return subtract((Expression) that);
        } else if (that instanceof JsclInteger || that instanceof Rational || that instanceof NumericWrapper) {
            return subtract(newInstance(that));
        } else {
            return that.newInstance(this).subtract(that);
        }
    }

    @NotNull
    public Expression multiply(@NotNull Expression that) {
        Expression result = newInstance(0);

        for (Summand summand : summands) {
            result = ExpressionSummizer.instance.sum(result, null, that, summand);
        }

        return result;
    }

    @NotNull
    public Generic multiply(@NotNull Generic that) {
        if (that instanceof Expression) {
            return multiply((Expression) that);
        } else if (that instanceof JsclInteger || that instanceof Rational || that instanceof NumericWrapper) {
            return multiply(newInstance(that));
        } else {
            return that.multiply(this);
        }
    }

    @NotNull
    public Generic divide(@NotNull Generic that) throws NotDivisibleException {
        final DivideAndRemainderResult darr = divideAndRemainder(that);
        if (darr.getRemainder().isZero()) {
            return darr.getDivisionResult();
        } else {
            throw new NotDivisibleException();
        }
    }

    @NotNull
    public DivideAndRemainderResult divideAndRemainder(@NotNull Generic that) throws ArithmeticException {
        if (that instanceof Expression) {
            return divideAndRemainder((Expression) that);
        } else if (that instanceof GenericInteger) {
            return divideAndRemainder((GenericInteger) that);
        } else if (that instanceof Rational || that instanceof NumericWrapper) {
            return divideAndRemainder(newInstance(that));
        } else {
            return that.newInstance(this).divideAndRemainder(that);
        }
    }

    @NotNull
    public DivideAndRemainderResult divideAndRemainder(@NotNull GenericInteger that) {
        try {
            final Builder b = new Builder(this.getSize());
            for (Summand summand : summands) {
                b.addSummand(summand.getCoefficient().divide(that), summand.getLiteral());
            }
            return DivideAndRemainderResult.newInstance(b.build(), GenericInteger.newInstance(0L));
        } catch (NotDivisibleException e) {
            return DivideAndRemainderResult.newInstance(GenericInteger.newInstance(0L), this);
        }
    }

    @NotNull
    private DivideAndRemainderResult divideAndRemainder(@NotNull Expression that) {
        Literal thisLiteral = this.literalScm();
        Literal thatLiteral = that.literalScm();

        final Literal gcd = thisLiteral.gcd(thatLiteral);

        Variable va[] = gcd.variables();
        if (va.length == 0) {
            if (signum() == 0 && ex.signum() != 0) return new Generic[]{this, JsclInteger.valueOf(0)};
            else try {
                return divideAndRemainder((Generic) ex.integerValue());
            } catch (NotIntegerException e) {
                return new Generic[]{JsclInteger.valueOf(0), this};
            }
        } else {
            Polynomial fact = Polynomial.factory(va[0]);
            Polynomial p[] = fact.valueOf(this).divideAndRemainder(fact.valueOf(ex));
            return new Generic[]{p[0].genericValue(), p[1].genericValue()};
        }
    }

    @NotNull
    public Generic gcd(@NotNull Generic generic) {
        if (generic instanceof Expression) {
            final Expression that = (Expression) generic;

            final Literal thisL = this.literalScm();
            final Literal thatL = that.literalScm();

            final Literal gcdL = thisL.gcd(thatL);

            final Variable vars[] = gcdL.variables();
            if (vars.length == 0) {
                if (signum() == 0) {
                    return that;
                } else {
                    return this.gcd(that.gcd());
                }
            } else {
                Polynomial p = Polynomial.factory(vars[0]);
                return p.valueOf(this).gcd(p.valueOf(that)).genericValue();
            }
        } else if (generic instanceof JsclInteger) {
            if (generic.signum() == 0) {
                return this;
            } else {
                return this.gcd().gcd(generic);
            }
        } else if (generic instanceof Rational || generic instanceof NumericWrapper) {
            return gcd(newInstance(generic));
        } else {
            return generic.newInstance(this).gcd(generic);
        }
    }

    @NotNull
    public Generic gcd() {
        JsclInteger result = JsclInteger.valueOf(0);

        for (int i = size - 1; i >= 0; i--) {
            result = result.gcd(coefficients[i]);
        }

        return result;
    }

    @NotNull
    public Literal literalScm() {
        Literal result = Literal.newEmpty();

        for (Summand summand : summands) {
            result = result.lcm(summand.getLiteral());
        }

        return result;
    }

    @NotNull
    public Generic negate() {
        return multiply(JsclInteger.valueOf(-1));
    }

    public int signum() {
        return size == 0 ? 0 : coefficients[0].signum();
    }

    public int degree() {
        return 0;
    }

    public Generic antiDerivative(@NotNull Variable variable) throws NotIntegrableException {
        if (isPolynomial(variable)) {
            return ((UnivariatePolynomial) Polynomial.factory(variable).valueOf(this)).antiderivative().genericValue();
        } else {
            try {
                Variable v = variableValue();
                try {
                    return v.antiDerivative(variable);
                } catch (NotIntegrableException e) {
                    if (v instanceof Fraction) {
                        Generic g[] = ((Fraction) v).getParameters();
                        if (g[1].isConstant(variable)) {
                            return new Inverse(g[1]).selfExpand().multiply(g[0].antiDerivative(variable));
                        }
                    }
                }
            } catch (NotVariableException e) {
                Generic sumElements[] = sumValue();
                if (sumElements.length > 1) {

                    Generic result = JsclInteger.valueOf(0);
                    for (Generic sumElement : sumElements) {
                        result = result.add(sumElement.antiDerivative(variable));
                    }
                    return result;

                } else {
                    final Generic products[] = sumElements[0].productValue();
                    Generic constantProduct = JsclInteger.valueOf(1);
                    Generic notConstantProduct = JsclInteger.valueOf(1);
                    for (Generic product : products) {
                        if (product.isConstant(variable)) {
                            constantProduct = constantProduct.multiply(product);
                        } else {
                            notConstantProduct = notConstantProduct.multiply(product);
                        }
                    }
                    if (constantProduct.compareTo(JsclInteger.valueOf(1)) != 0) {
                        return constantProduct.multiply(notConstantProduct.antiDerivative(variable));
                    }
                }
            }
        }
        throw new NotIntegrableException(this);
    }

    public Generic derivative(@NotNull Variable variable) {
        Generic s = JsclInteger.valueOf(0);
        Literal l = literalScm();
        int n = l.size();
        for (int i = 0; i < n; i++) {
            Variable v = l.getVariable(i);
            Generic a = ((UnivariatePolynomial) Polynomial.factory(v).valueOf(this)).derivative(variable).genericValue();
            s = s.add(a);
        }
        return s;
    }

    public Generic substitute(@NotNull final Variable variable, final Generic generic) {
        final Map<Variable, Generic> content = literalScm().content(new Converter<Variable, Generic>() {
            @NotNull
            @Override
            public Generic convert(@NotNull Variable v) {
                return v.substitute(variable, generic);
            }
        });

        return substitute(content);
    }

    @NotNull
    private Generic substitute(@NotNull Map<Variable, Generic> content) {
        // sum = sumElement_0 + sumElement_1 + ... + sumElement_size
        Generic sum = JsclInteger.ZERO;

        for (int i = 0; i < size; i++) {
            final Literal literal = literals[i];

            // sumElement = variable_1 ^ power_1 * variable_2 ^ power_2 * ... * variable_size ^ power_size
            Generic sumElement = coefficients[i];

            for (int j = 0; j < literal.size(); j++) {
                final Variable variable = literal.getVariable(j);

                Generic b = content.get(variable).pow(literal.getPower(j));

                if (Matrix.isMatrixProduct(sumElement, b)) {
                    throw new ArithmeticException("Should not be matrix!");
                }

                sumElement = sumElement.multiply(b);
            }

            sum = sum.add(sumElement);
        }

        return sum;
    }

    public Generic expand() {
        return substitute(literalScm().content(EXPAND_CONVERTER));
    }

    public Generic factorize() {
        return Factorization.compute(substitute(literalScm().content(FACTORIZE_CONVERTER)));
    }

    public Generic elementary() {
        return substitute(literalScm().content(ELEMENTARY_CONVERTER));
    }

    public Generic simplify() {
        return Simplification.compute(this);
    }

    public Generic numeric() {
        try {
            return integerValue().numeric();
        } catch (NotIntegerException ex) {
            final Literal literal = literalScm();

            final Map<Variable, Generic> content = literal.content(NUMERIC_CONVERTER);

            return substitute(content);
        }
    }

    @NotNull
    public Expression newInstance(@NotNull Generic generic) {
        final Builder result = new Builder(1);

        result.addSummand(Summand.newInstance(GenericInteger.newInstance(1L), Literal.Builder));

        return result;
    }

    @NotNull
    public Generic[] sumValue() {
        final Generic result[] = new Generic[size];

        for (int i = 0; i < result.length; i++) {
            result[i] = valueOf(literals[i], coefficients[i]);
        }

        return result;
    }

    @NotNull
    public Generic[] productValue() throws NotProductException {
        if (size == 0) {
            return new Generic[]{JsclInteger.valueOf(0)};
        } else if (size == 1) {
            final Literal l = literals[0];
            final JsclInteger k = coefficients[0];

            Generic productElements[] = l.productValue();
            if (k.compareTo(JsclInteger.valueOf(1)) == 0) {
                return productElements;
            } else {
                final Generic result[] = new Generic[productElements.length + 1];
                System.arraycopy(productElements, 0, result, 1, productElements.length);
                result[0] = k;
                return result;
            }
        } else {
            throw new NotProductException();
        }
    }

    public Power powerValue() throws NotPowerException {
        if (size == 0) return new Power(JsclInteger.valueOf(0), 1);
        else if (size == 1) {
            Literal l = literals[0];
            JsclInteger en = coefficients[0];
            if (en.compareTo(JsclInteger.valueOf(1)) == 0) return l.powerValue();
            else if (l.getDegree() == 0) return en.powerValue();
            else throw new NotPowerException();
        } else throw new NotPowerException();
    }

    public Expression expressionValue() throws NotExpressionException {
        return this;
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

    public JsclInteger integerValue() throws NotIntegerException {
        if (size == 0) {
            return JsclInteger.valueOf(0);
        } else if (size == 1) {

            final Literal l = literals[0];
            final JsclInteger c = coefficients[0];

            if (l.getDegree() == 0) {
                return c;
            } else {
                throw new NotIntegerException();
            }
        } else {
            throw new NotIntegerException();
        }
    }

    public Variable variableValue() throws NotVariableException {
        if (size == 0) {
            throw new NotVariableException();
        } else if (size == 1) {
            final Literal l = literals[0];
            final JsclInteger c = coefficients[0];
            if (c.compareTo(JsclInteger.valueOf(1)) == 0) {
                return l.variableValue();
            } else {
                throw new NotVariableException();
            }
        } else {
            throw new NotVariableException();
        }
    }

    public Variable[] variables() {
        return literalScm().variables();
    }

    public static Variable[] variables(Generic elements[]) {
        final List<Variable> result = new ArrayList<Variable>();

        for (Generic element : elements) {
            for (Variable variable : element.variables()) {
                if (!result.contains(variable)) {
                    result.add(variable);
                }
            }
        }

        return ArrayUtils.toArray(result, new Variable[result.size()]);
    }

    public boolean isPolynomial(@NotNull Variable variable) {
        boolean result = true;

        final Literal l = literalScm();
        for (int i = 0; i < l.size(); i++) {

            final Variable v = l.getVariable(i);
            if (!v.isConstant(variable) && !v.isIdentity(variable)) {
                result = false;
                break;
            }
        }

        return result;
    }

    public boolean isConstant(@NotNull Variable variable) {

        Literal l = literalScm();
        for (int i = 0; i < l.size(); i++) {
            if (!l.getVariable(i).isConstant(variable)) {
                return false;
            }
        }

        return true;
    }

    public JsclVector grad(Variable variable[]) {
        Generic v[] = new Generic[variable.length];
        for (int i = 0; i < variable.length; i++) v[i] = derivative(variable[i]);
        return new JsclVector(v);
    }

    public Generic laplacian(Variable variable[]) {
        return grad(variable).divergence(variable);
    }

    public Generic dalembertian(Variable variable[]) {
        Generic a = derivative(variable[0]).derivative(variable[0]);
        for (int i = 1; i < 4; i++) a = a.subtract(derivative(variable[i]).derivative(variable[i]));
        return a;
    }

    public int compareTo(Expression expression) {
        int i1 = size;
        int i2 = expression.size;
        Literal l1 = i1 == 0 ? null : literals[--i1];
        Literal l2 = i2 == 0 ? null : expression.literals[--i2];
        while (l1 != null || l2 != null) {
            int c = l1 == null ? -1 : (l2 == null ? 1 : l1.compareTo(l2));
            if (c < 0) return -1;
            else if (c > 0) return 1;
            else {
                c = coefficients[i1].compareTo(expression.coefficients[i2]);
                if (c < 0) return -1;
                else if (c > 0) return 1;
                l1 = i1 == 0 ? null : literals[--i1];
                l2 = i2 == 0 ? null : expression.literals[--i2];
            }
        }
        return 0;
    }

    public int compareTo(@NotNull Generic generic) {
        if (generic instanceof Expression) {
            return compareTo((Expression) generic);
        } else if (generic instanceof JsclInteger || generic instanceof Rational || generic instanceof NumericWrapper) {
            return compareTo(newInstance(generic));
        } else {
            return generic.newInstance(this).compareTo(generic);
        }
    }

    @NotNull
    public static Expression valueOf(@NotNull Variable variable) {
        return valueOf(Literal.valueOf(variable));
    }

    @NotNull
    public static Expression valueOf(@NotNull Literal literal) {
        return valueOf(literal, JsclInteger.valueOf(1));
    }

    @NotNull
    public static Expression valueOf(@NotNull JsclInteger integer) {
        return valueOf(Literal.newInstance(), integer);
    }

    @NotNull
    public static Expression valueOf(@NotNull Literal literal, @NotNull JsclInteger integer) {
        final Expression result = new Expression();
        result.init(literal, integer);
        return result;
    }

    void init(Literal lit, JsclInteger integer) {
        if (integer.signum() != 0) {
            init(1);
            literals[0] = lit;
            coefficients[0] = integer;
        } else init(0);
    }

    public static Expression valueOf(Rational rational) {
        Expression ex = new Expression();
        ex.init(rational);
        return ex;
    }

    public static Expression valueOf(@NotNull Constant constant) {
        final Expression expression = new Expression(1);
        Literal literal = new Literal(productands);
        literal.init(constant, 1);
        expression.init(literal, JsclInteger.ONE);
        return expression;
    }

    public static Expression valueOf(@NotNull Double value) {
        final Expression expression = new Expression(1);
        Literal literal = new Literal(productands);
        literal.init(new DoubleVariable(new NumericWrapper(Real.valueOf(value))), 1);
        expression.init(literal, JsclInteger.ONE);
        return expression;
    }

    public static Expression valueOf(@NotNull String expression) throws ParseException {
        final MutableInt position = new MutableInt(0);

        final Parser.Parameters p = Parser.Parameters.newInstance(expression, position, JsclMathEngine.instance);

        final Generic generic = ExpressionParser.parser.parse(p, null);

        ParserUtils.skipWhitespaces(p);

        int index = position.intValue();
        if (index < expression.length()) {
            throw new ParseException(Messages.msg_1, index, expression, index + 1);
        }

        return new Expression().init(generic);
    }

    public static Expression init(@NotNull NumericWrapper numericWrapper) {
        final Expression expression = new Expression(1);
        Literal literal = new Literal(productands);
        literal.init(new ExpressionVariable(numericWrapper), 1);
        expression.init(literal, JsclInteger.ONE);
        return expression;
    }

    void init(Expression expression) {
        init(expression.size);
        System.arraycopy(expression.literals, 0, literals, 0, size);
        System.arraycopy(expression.coefficients, 0, coefficients, 0, size);
    }

    void init(JsclInteger integer) {
        init(Literal.newInstance(), integer);
    }

    void init(Rational rational) {
        try {
            init(Literal.newInstance(), rational.integerValue());
        } catch (NotIntegerException e) {
            init(Literal.valueOf(rational.variableValue()), JsclInteger.valueOf(1));
        }
    }

    Expression init(@NotNull Generic generic) {
        if (generic instanceof Expression) {
            init((Expression) generic);
        } else if (generic instanceof JsclInteger) {
            init((JsclInteger) generic);
        } else if (generic instanceof NumericWrapper) {
            init((NumericWrapper) generic);
        } else if (generic instanceof Rational) {
            init((Rational) generic);
        } else throw new ArithmeticException("Could not initialize expression with " + generic.getClass());

        return this;
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();

        if (signum() == 0) {
            result.append("0");
        }

        // result = coef[0] * literal[0] + coef[1] * literal[1] + ... +
        for (int i = 0; i < size; i++) {
            final Literal literal = literals[i];
            final JsclInteger coefficient = coefficients[i];

            if (coefficient.signum() > 0 && i > 0) {
                result.append("+");
            }

            if (literal.getDegree() == 0) {
                result.append(coefficient);
            } else {
                if (coefficient.abs().compareTo(JsclInteger.ONE) == 0) {
                    if (coefficient.signum() < 0) {
                        result.append("-");
                    }
                } else {
                    result.append(coefficient).append("*");
                }
                result.append(literal);
            }
        }

        return result.toString();
    }

    public String toJava() {
        final StringBuilder result = new StringBuilder();
        if (signum() == 0) {
            result.append("JsclDouble.valueOf(0)");
        }

        for (int i = 0; i < size; i++) {
            Literal l = literals[i];
            JsclInteger en = coefficients[i];
            if (i > 0) {
                if (en.signum() < 0) {
                    result.append(".subtract(");
                    en = (JsclInteger) en.negate();
                } else result.append(".add(");
            }
            if (l.getDegree() == 0) result.append(en.toJava());
            else {
                if (en.abs().compareTo(JsclInteger.valueOf(1)) == 0) {
                    if (en.signum() > 0) result.append(l.toJava());
                    else if (en.signum() < 0) result.append(l.toJava()).append(".negate()");
                } else result.append(en.toJava()).append(".multiply(").append(l.toJava()).append(")");
            }
            if (i > 0) result.append(")");
        }

        return result.toString();
    }

    public void toMathML(MathML element, @Nullable Object data) {
        MathML e1 = element.newElement("mrow");
        if (signum() == 0) {
            MathML e2 = element.newElement("mn");
            e2.appendChild(element.newText("0"));
            e1.appendChild(e2);
        }
        for (int i = 0; i < size; i++) {
            Literal l = literals[i];
            JsclInteger en = coefficients[i];
            if (en.signum() > 0 && i > 0) {
                MathML e2 = element.newElement("mo");
                e2.appendChild(element.newText("+"));
                e1.appendChild(e2);
            }
            if (l.getDegree() == 0) separateSign(e1, en);
            else {
                if (en.abs().compareTo(JsclInteger.valueOf(1)) == 0) {
                    if (en.signum() < 0) {
                        MathML e2 = element.newElement("mo");
                        e2.appendChild(element.newText("-"));
                        e1.appendChild(e2);
                    }
                } else separateSign(e1, en);
                l.toMathML(e1, null);
            }
        }
        element.appendChild(e1);
    }

/*	@NotNull
	@Override
	public Set<? extends Constant> getConstants() {
		final Set<Constant> result = new HashSet<Constant>();

		for (Literal literal : literals) {
			for (Variable variable : literal.variables()) {
				result.addAll(variable.getConstants());
			}
		}

		return result;
	}*/

    public static void separateSign(MathML element, Generic generic) {
        if (generic.signum() < 0) {
            MathML e1 = element.newElement("mo");
            e1.appendChild(element.newText("-"));
            element.appendChild(e1);
            generic.negate().toMathML(element, null);
        } else {
            generic.toMathML(element, null);
        }
    }

    @NotNull
    private Expression newInstance(int n) {
        return new Expression(n);
    }
}
