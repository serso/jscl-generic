package jscl.math.generic.expression;

import jscl.ImmutableObjectBuilder;
import jscl.JsclMathEngine;
import jscl.math.NotDivisibleException;
import jscl.math.Transformable;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.function.Fraction;
import jscl.math.function.Inverse;
import jscl.math.generic.*;
import jscl.math.generic.expression.literal.Literal;
import jscl.math.generic.expression.literal.Productand;
import jscl.math.numeric.Real;
import jscl.math.numeric.matrix.Matrix;
import jscl.math.polynomial.Polynomial;
import jscl.math.polynomial.UnivariatePolynomial;
import jscl.mathml.MathML;
import jscl.newText.*;
import jscl.newText.msg.Messages;
import jscl.util.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Converter;

import java.util.*;

public class Expression extends Generic {

    private static final Expression EMPTY_EXPRESSION = new Expression(Collections.<Summand>emptyList());

    @NotNull
    private final List<Summand> summands;

    // for fast access
    @Nullable
    private Literal literalLcm;

    /*
   * *********************************************************************
   *
   *                         CONSTRUCTORS
   *
   * *********************************************************************
    */

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

    private Expression(@NotNull List<Summand> summands) {
        this.summands = summands;
    }

    @NotNull
    public Expression newInstance(@NotNull Generic generic) {
        final Builder result = new Builder(1);

        result.addSummand(Summand.newInstance(GenericInteger.newInstance(1L), ));

        return result;
    }

    @NotNull
    private static Expression newEmpty() {
        return EMPTY_EXPRESSION;
    }

    /*
   * *********************************************************************
   *
   *                         GETTERS
   *
   * *********************************************************************
    */

    public int getSize() {
        return this.summands.size();
    }

    @NotNull
    public List<Summand> getSummands() {
        return Collections.unmodifiableList(summands);
    }

    /*
   * *********************************************************************
   *
   *                         ADDITION
   *
   * *********************************************************************
    */

    @NotNull
    public Expression add(@NotNull Expression that) {
        return ExpressionSummizer.instance.sum(this, that);
    }

    @NotNull
    public Expression add(@NotNull GenericInteger that) {
        return add(newInstance(that));
    }

    @NotNull
    public Expression add(@NotNull Rational that) {
        return add(newInstance(that));
    }

    @NotNull
    public Expression add(@NotNull GenericNumeric that) {
        return add(newInstance(that));
    }

    @NotNull
    public Generic add(@NotNull Generic that) {
        if (that instanceof Expression) {
            return add((Expression) that);
        } else if (that instanceof GenericInteger) {
            return add((GenericInteger) that);
        } else if (that instanceof Rational) {
            return add((Rational) that);
        } else if (that instanceof GenericNumeric) {
            return add((GenericNumeric) that);
        } else {
            return that.newInstance(this).add(that);
        }
    }

    /*
   * *********************************************************************
   *
   *                         SUBTRACTION
   *
   * *********************************************************************
    */

    @NotNull
    public Expression subtract(@NotNull Expression that) {
        return ExpressionSummizer.instance.sum(this, null, that, Summand.newInstance(GenericInteger.newInstance(-1L)));
    }

    @NotNull
    public Expression subtract(@NotNull GenericInteger that) {
        return subtract(newInstance(that));
    }

    @NotNull
    public Expression subtract(@NotNull Rational that) {
        return subtract(newInstance(that));
    }

    @NotNull
    public Expression subtract(@NotNull GenericNumeric that) {
        return subtract(newInstance(that));
    }

    @NotNull
    public Generic subtract(@NotNull Generic that) {
        if (that instanceof Expression) {
            return subtract((Expression) that);
        } else if (that instanceof GenericInteger) {
            return subtract((GenericInteger) that);
        } else if (that instanceof Rational) {
            return subtract((Rational) that);
        } else if (that instanceof GenericNumeric) {
            return subtract((GenericNumeric) that);
        } else {
            return that.newInstance(this).subtract(that);
        }
    }

    /*
   * *********************************************************************
   *
   *                         MULTIPLICATION
   *
   * *********************************************************************
    */

    @NotNull
    public Expression multiply(@NotNull Expression that) {
        Expression result = newEmpty();

        for (Summand summand : summands) {
            result = ExpressionSummizer.instance.sum(result, null, that, summand);
        }

        return result;
    }

    @NotNull
    public Expression multiply(@NotNull GenericInteger that) {
        return multiply(newInstance(that));
    }

    @NotNull
    public Expression multiply(@NotNull Rational that) {
        return multiply(newInstance(that));
    }

    @NotNull
    public Expression multiply(@NotNull GenericNumeric that) {
        return multiply(newInstance(that));
    }

    @NotNull
    public Generic multiply(@NotNull Generic that) {
        if (that instanceof Expression) {
            return multiply((Expression) that);
        } else if (that instanceof GenericInteger) {
            return multiply((GenericInteger) that);
        } else if (that instanceof Rational) {
            return multiply((Rational) that);
        } else if (that instanceof GenericNumeric) {
            return multiply((GenericNumeric) that);
        } else {
            return that.multiply(this);
        }
    }

    /*
   * *********************************************************************
   *
   *                         DIVISION
   *
   * *********************************************************************
    */

    @NotNull
    public Generic divide(@NotNull Generic that) throws NotDivisibleException {
        final DivideAndRemainderResult darr = divideAndRemainder(that);
        if (darr.getRemainder().isZero()) {
            // remainder 0 => can be divided
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
        } else if (that instanceof Rational) {
            return divideAndRemainder((Rational) that);
        } else if (that instanceof GenericNumeric) {
            return divideAndRemainder((GenericNumeric) that);
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
    public DivideAndRemainderResult divideAndRemainder(@NotNull Rational that) {
        return divideAndRemainder(newInstance(that));
    }

    @NotNull
    public DivideAndRemainderResult divideAndRemainder(@NotNull GenericNumeric that) {
        return divideAndRemainder(newInstance(that));
    }

    @NotNull
    private DivideAndRemainderResult divideAndRemainder(@NotNull Expression that) {
        Literal thisLiteral = this.literalLcm();
        Literal thatLiteral = that.literalLcm();

        final Literal gcd = thisLiteral.gcd(thatLiteral);

        final List<Variable> variables = gcd.getVariables();
        if (variables.size() == 0) {
            if (this.signum() == 0 && that.signum() != 0) {
                return DivideAndRemainderResult.newInstance(this, GenericInteger.ZERO);
            } else {
                try {
                    return divideAndRemainder((Generic) that.integerValue());
                } catch (NotIntegerException e) {
                    return DivideAndRemainderResult.newInstance(GenericInteger.ZERO, this);
                }
            }
        } else {
            Polynomial fact = Polynomial.factory(variables[0]);
            Polynomial p[] = fact.valueOf(this).divideAndRemainder(fact.valueOf(ex));
            return new Generic[]{p[0].genericValue(), p[1].genericValue()};
        }
    }

    @NotNull
    public Generic gcd(@NotNull Generic generic) {
        if (generic instanceof Expression) {
            final Expression that = (Expression) generic;

            final Literal thisL = this.literalLcm();
            final Literal thatL = that.literalLcm();

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
        } else if (generic instanceof GenericInteger) {
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
    public GenericInteger gcd() {
        GenericInteger result = GenericInteger.ZERO;

        for (Summand summand : CollectionsUtils.reversed(summands)) {
            result = result.gcd(summand.getCoefficient());
        }

        return result;
    }

    @NotNull
    public Literal literalLcm() {
        Literal localLiteralLcm = literalLcm;

        if (localLiteralLcm == null) {
            localLiteralLcm = Literal.newEmpty();

            for (Summand summand : summands) {
                localLiteralLcm = localLiteralLcm.lcm(summand.getLiteral());
            }

            // do not care about synchronization as all objects are immutable
            literalLcm = localLiteralLcm;
        }

        return localLiteralLcm;
    }

    @NotNull
    public Generic negate() {
        return multiply(GenericInteger.newInstance(-1L));
    }

    public int signum() {
        return getSize() == 0 ? 0 : summands.get(0).getCoefficient().signum();
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

                    Generic result = GenericInteger.valueOf(0);
                    for (Generic sumElement : sumElements) {
                        result = result.add(sumElement.antiDerivative(variable));
                    }
                    return result;

                } else {
                    final Generic products[] = sumElements[0].productValue();
                    Generic constantProduct = GenericInteger.valueOf(1);
                    Generic notConstantProduct = GenericInteger.valueOf(1);
                    for (Generic product : products) {
                        if (product.isConstant(variable)) {
                            constantProduct = constantProduct.multiply(product);
                        } else {
                            notConstantProduct = notConstantProduct.multiply(product);
                        }
                    }
                    if (constantProduct.compareTo(GenericInteger.valueOf(1)) != 0) {
                        return constantProduct.multiply(notConstantProduct.antiDerivative(variable));
                    }
                }
            }
        }
        throw new NotIntegrableException(this);
    }

    public Generic derivative(@NotNull Variable variable) {
        Generic s = GenericInteger.valueOf(0);
        Literal l = literalLcm();
        int n = l.size();
        for (int i = 0; i < n; i++) {
            Variable v = l.getVariable(i);
            Generic a = ((UnivariatePolynomial) Polynomial.factory(v).valueOf(this)).derivative(variable).genericValue();
            s = s.add(a);
        }
        return s;
    }

    public Generic substitute(@NotNull final Variable variable, final Generic generic) {

        final Map<Variable, Generic> content = literalLcm().content(new Converter<Transformable, Generic>() {
            @NotNull
            @Override
            public Generic convert(@NotNull Transformable v) {
                // todo serso: check cast
                return ((Variable) v).substitute(variable, generic);
            }
        });

        return substitute(content);
    }

    @NotNull
    private Generic substitute(@NotNull Map<Variable, Generic> content) {
        // sum = sumElement_0 + sumElement_1 + ... + sumElement_size
        Generic sum = GenericInteger.ZERO;

        for (Summand summand : summands) {
            final Literal literal = summand.getLiteral();

            // sumElement = variable_1 ^ power_1 * variable_2 ^ power_2 * ... * variable_size ^ power_size
            Generic sumElement = summand.getCoefficient();

            for (Productand productand : literal.getProductands()) {
                final Variable variable = productand.getVariable();

                Generic b = content.get(variable).pow(productand.getExponent());

                if (Matrix.isMatrixProduct(sumElement, b)) {
                    throw new ArithmeticException("Should not be matrix!");
                }

                sumElement = sumElement.multiply(b);
            }

            sum = sum.add(sumElement);
        }


        return sum;
    }

    @NotNull
    public Generic expand() {
        return substitute(literalLcm().content(Transformable.EXPAND_CONVERTER));
    }

    @NotNull
    public Generic factorize() {
        return Factorization.compute(substitute(literalLcm().content(Transformable.FACTORIZE_CONVERTER)));
    }

    @NotNull
    public Generic elementary() {
        return substitute(literalLcm().content(Transformable.ELEMENTARY_CONVERTER));
    }

    @NotNull
    public Generic simplify() {
        return Simplification.compute(this);
    }

    @NotNull
    public Generic numeric() {
        try {
            return integerValue().numeric();
        } catch (NotIntegerException ex) {
            final Literal literal = literalLcm();

            final Map<Variable, Generic> content = literal.content(NUMERIC_CONVERTER);

            return substitute(content);
        }
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
            return new Generic[]{GenericInteger.valueOf(0)};
        } else if (size == 1) {
            final Literal l = literals[0];
            final GenericInteger k = coefficients[0];

            Generic productElements[] = l.productValue();
            if (k.compareTo(GenericInteger.valueOf(1)) == 0) {
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
        if (size == 0) return new Power(GenericInteger.valueOf(0), 1);
        else if (size == 1) {
            Literal l = literals[0];
            GenericInteger en = coefficients[0];
            if (en.compareTo(GenericInteger.valueOf(1)) == 0) return l.powerValue();
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

    public GenericInteger integerValue() throws NotIntegerException {
        if (size == 0) {
            return GenericInteger.valueOf(0);
        } else if (size == 1) {

            final Literal l = literals[0];
            final GenericInteger c = coefficients[0];

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
            final GenericInteger c = coefficients[0];
            if (c.compareTo(GenericInteger.valueOf(1)) == 0) {
                return l.variableValue();
            } else {
                throw new NotVariableException();
            }
        } else {
            throw new NotVariableException();
        }
    }

    @NotNull
    public Set<Variable> variables() {
        return literalLcm().getVariables();
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

        final Literal l = literalLcm();
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

        Literal l = literalLcm();
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
        } else if (generic instanceof GenericInteger || generic instanceof Rational || generic instanceof NumericWrapper) {
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
        return valueOf(literal, GenericInteger.valueOf(1));
    }

    @NotNull
    public static Expression valueOf(@NotNull GenericInteger integer) {
        return valueOf(Literal.newInstance(), integer);
    }

    @NotNull
    public static Expression valueOf(@NotNull Literal literal, @NotNull GenericInteger integer) {
        final Expression result = new Expression();
        result.init(literal, integer);
        return result;
    }

    void init(Literal lit, GenericInteger integer) {
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
        expression.init(literal, GenericInteger.ONE);
        return expression;
    }

    public static Expression valueOf(@NotNull Double value) {
        final Expression expression = new Expression(1);
        Literal literal = new Literal(productands);
        literal.init(new DoubleVariable(new NumericWrapper(Real.valueOf(value))), 1);
        expression.init(literal, GenericInteger.ONE);
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
        expression.init(literal, GenericInteger.ONE);
        return expression;
    }

    void init(Expression expression) {
        init(expression.size);
        System.arraycopy(expression.literals, 0, literals, 0, size);
        System.arraycopy(expression.coefficients, 0, coefficients, 0, size);
    }

    void init(GenericInteger integer) {
        init(Literal.newInstance(), integer);
    }

    void init(Rational rational) {
        try {
            init(Literal.newInstance(), rational.integerValue());
        } catch (NotIntegerException e) {
            init(Literal.valueOf(rational.variableValue()), GenericInteger.valueOf(1));
        }
    }

    Expression init(@NotNull Generic generic) {
        if (generic instanceof Expression) {
            init((Expression) generic);
        } else if (generic instanceof GenericInteger) {
            init((GenericInteger) generic);
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
            final GenericInteger coefficient = coefficients[i];

            if (coefficient.signum() > 0 && i > 0) {
                result.append("+");
            }

            if (literal.getDegree() == 0) {
                result.append(coefficient);
            } else {
                if (coefficient.abs().compareTo(GenericInteger.ONE) == 0) {
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
            GenericInteger en = coefficients[i];
            if (i > 0) {
                if (en.signum() < 0) {
                    result.append(".subtract(");
                    en = (GenericInteger) en.negate();
                } else result.append(".add(");
            }
            if (l.getDegree() == 0) result.append(en.toJava());
            else {
                if (en.abs().compareTo(GenericInteger.valueOf(1)) == 0) {
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
            GenericInteger en = coefficients[i];
            if (en.signum() > 0 && i > 0) {
                MathML e2 = element.newElement("mo");
                e2.appendChild(element.newText("+"));
                e1.appendChild(e2);
            }
            if (l.getDegree() == 0) separateSign(e1, en);
            else {
                if (en.abs().compareTo(GenericInteger.valueOf(1)) == 0) {
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
