package jscl.math.generic.expression;

import jscl.ImmutableObjectBuilder;
import jscl.math.NotDivisibleException;
import jscl.math.NotIntegrableException;
import jscl.math.Transformable;
import jscl.math.Variable;
import jscl.math.generic.*;
import jscl.math.generic.expression.literal.Literal;
import jscl.math.generic.expression.literal.Productand;
import jscl.mathml.MathML;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Converter;

import java.util.*;

public class Expression extends Generic implements Iterable<Summand> {

    @NotNull
    private final List<Summand> summands;

    // for fast access
    @Nullable
    private Literal literalLcm;

    @Override
    public Iterator<Summand> iterator() {
        return this.summands.iterator();
    }

    @NotNull
    public Summand getSummand(int i) {
        return this.summands.get(i);
    }

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

        @NotNull
        private final GenericContext context;

        public Builder(@NotNull GenericContext context, int initialCapacity) {
            this.context = context;
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

            return new Expression(this.summands, context);
        }
    }

    private Expression(@NotNull List<Summand> summands, @NotNull GenericContext context) {
        super(context);
        this.summands = summands;
    }

    @NotNull
    public Expression newInstance(@NotNull Generic that) {
        if (that instanceof Expression) {
            return (Expression) that;
        } else if (that instanceof GenericInteger) {
            return newInstance((GenericInteger) that);
        } else if (that instanceof Rational) {
            return newInstance((Rational) that);
        } else if (that instanceof GenericNumeric) {
            return newInstance((GenericNumeric) that);
        } else {
            throw new ArithmeticException("Could not initialize expression with " + that.getClass());
        }
    }

    @NotNull
    public static Expression newInstance(@NotNull GenericInteger that) {
        return newInstance0(that);
    }

    @NotNull
    public static Expression newInstance(@NotNull Rational that) {
        return newInstance0(that);
    }

    @NotNull
    public static Expression newInstance(@NotNull GenericNumeric that) {
        return newInstance0(that);
    }

    @NotNull
    private static Expression newInstance0(@NotNull Generic that) {
        if (!that.isZero()) {
            final Builder b = new Builder(that.getContext(), 1);
            try {
                b.addSummand(Summand.newInstance(that.integerValue()));
            } catch (NotIntegerException e) {
                b.addSummand(Summand.newInstance(that.getContext().getOne(), Literal.newInstance(that.variableValue())));
            }
            return b.build();
        } else {
            return Expression.newEmpty(that.getContext());
        }
    }

    @NotNull
    private static Expression newEmpty(@NotNull GenericContext context) {
        return context.newEmptyExpression();
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
        return ExpressionSummizer.instance.sum(this, null, that, Summand.newInstance(context.newInteger(-1L)));
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
        Expression result = newEmpty(context);

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
            final Builder b = new Builder(context, this.getSize());
            for (Summand summand : summands) {
                b.addSummand(summand.getCoefficient().divide(that), summand.getLiteral());
            }
            return DivideAndRemainderResult.newInstance(b.build(), context.getZero());
        } catch (NotDivisibleException e) {
            return DivideAndRemainderResult.newInstance(context.getZero(), this);
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
                return DivideAndRemainderResult.newInstance(this, context.getZero());
            } else {
                try {
                    return divideAndRemainder((Generic) that.integerValue());
                } catch (NotIntegerException e) {
                    return DivideAndRemainderResult.newInstance(context.getZero(), this);
                }
            }
        } else {
            throw new UnsupportedOperationException();

            /*
                           Polynomial fact = Polynomial.factory(variables[0]);
                           Polynomial p[] = fact.valueOf(this).divideAndRemainder(fact.valueOf(ex));
                           return new Generic[]{p[0].genericValue(), p[1].genericValue()};*/
        }
    }

    /*
   * *********************************************************************
   *
   *                         GCD
   *
   * *********************************************************************
    */

    @NotNull
    public Generic gcd(@NotNull Generic generic) {
        if (generic instanceof Expression) {
            return ExpressionGcd.instance.gcd(this, (Expression) generic);
        } else if (generic instanceof GenericInteger) {
            if (generic.signum() == 0) {
                return this;
            } else {
                return this.getIntegerGcd().gcd(generic);
            }
        } else if (generic instanceof Rational || generic instanceof GenericNumeric) {
            return gcd(newInstance(generic));
        } else {
            return generic.newInstance(this).gcd(generic);
        }
    }

    @NotNull
    public GenericInteger getIntegerGcd() {
        GenericInteger result = context.getZero();

        for (Summand summand : CollectionsUtils.reversed(summands)) {
            result = result.gcd(summand.getCoefficient());
        }

        return result;
    }

    /*
    **********************************************************************
    *
    *                           LCM
    *
    **********************************************************************
    */

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
        return multiply(context.newInteger(-1L));
    }

    public int signum() {
        return getSize() == 0 ? 0 : summands.get(0).getCoefficient().signum();
    }

    public int degree() {
        return 0;
    }

    @NotNull
    @Override
    public Generic valueOf(@NotNull Generic that) {
        return newInstance(that);
    }

    public Generic antiDerivative(@NotNull Variable v) throws NotIntegrableException {
        /*if (isPolynomial(v)) {
            return ((UnivariatePolynomial) Polynomial.factory(v).valueOf(this)).antiderivative().genericValue();
        } else {
            try {
                Variable v = variableValue();
                try {
                    return v.antiDerivative(v);
                } catch (NotIntegrableException e) {
                    if (v instanceof Fraction) {
                        Generic g[] = ((Fraction) v).getParameters();
                        if (g[1].isConstant(v)) {
                            return new Inverse(g[1]).selfExpand().multiply(g[0].antiDerivative(v));
                        }
                    }
                }
            } catch (NotVariableException e) {
                Generic sumElements[] = sumValue();
                if (sumElements.length > 1) {

                    Generic result = GenericInteger.valueOf(0);
                    for (Generic sumElement : sumElements) {
                        result = result.add(sumElement.antiDerivative(v));
                    }
                    return result;

                } else {
                    final Generic products[] = sumElements[0].productValue();
                    Generic constantProduct = GenericInteger.valueOf(1);
                    Generic notConstantProduct = GenericInteger.valueOf(1);
                    for (Generic product : products) {
                        if (product.isConstant(v)) {
                            constantProduct = constantProduct.multiply(product);
                        } else {
                            notConstantProduct = notConstantProduct.multiply(product);
                        }
                    }
                    if (constantProduct.compareTo(GenericInteger.valueOf(1)) != 0) {
                        return constantProduct.multiply(notConstantProduct.antiDerivative(v));
                    }
                }
            }
        }
*/
        throw new NotIntegrableException(this, v);
    }

    public Generic derivative(@NotNull Variable variable) {
        Generic result = context.getZero();

        for (Productand productand : literalLcm()) {
            Variable v = productand.getVariable();
            throw new UnsupportedOperationException();
            //Generic a = ((UnivariatePolynomial) Polynomial.factory(v).valueOf(this)).derivative(variable).genericValue();
            //result = result.add(a);
        }
        
        return result;
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
        Generic sum = context.getZero();

        for (Summand summand : summands) {
            final Literal literal = summand.getLiteral();

            // sumElement = variable_1 ^ power_1 * variable_2 ^ power_2 * ... * variable_size ^ power_size
            Generic sumElement = summand.getCoefficient();

            for (Productand productand : literal.getProductands()) {
                final Variable variable = productand.getVariable();

                Generic b = content.get(variable).pow(productand.getExponent());

/*                if (Matrix.isMatrixProduct(sumElement, b)) {
                    throw new ArithmeticException("Should not be matrix!");
                }*/

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
        throw new UnsupportedOperationException();
        //return Factorization.compute(substitute(literalLcm().content(Transformable.FACTORIZE_CONVERTER)));
    }

    @NotNull
    public Generic elementary() {
        return substitute(literalLcm().content(Transformable.ELEMENTARY_CONVERTER));
    }

    @NotNull
    public Generic simplify() {
        throw new UnsupportedOperationException();
        //return Simplification.compute(this);
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
    public List<Expression> sumValue() {
        final List<Expression> result = new ArrayList<Expression>(getSize());

        for (Summand summand : summands) {
            final Builder b = new Builder(context, 1);
            b.addSummand(summand);
            result.add(b.build());
        }

        return result;
    }

    @NotNull
    public List<? extends Generic> productValue() throws NotProductException {
        int size = getSize();
        if (size == 0) {
            return Arrays.asList(context.getZero());
        } else if (size == 1) {
            final Summand s = this.summands.get(0);
            return s.productValue();
        } else {
            throw new NotProductException();
        }
    }

/*    public Power powerValue() throws NotPowerException {
        if (size == 0) return new Power(GenericInteger.valueOf(0), 1);
        else if (size == 1) {
            Literal l = literals[0];
            GenericInteger en = coefficients[0];
            if (en.compareTo(GenericInteger.valueOf(1)) == 0) return l.powerValue();
            else if (l.getDegree() == 0) return en.powerValue();
            else throw new NotPowerException();
        } else throw new NotPowerException();
    }*/

    public Expression expressionValue() throws NotExpressionException {
        return this;
    }

/*    @Override
    public boolean isInteger() {
        try {
            integerValue();
            return true;
        } catch (NotIntegerException e) {
            return false;
        }
    }*/

    @NotNull
    public GenericInteger integerValue() throws NotIntegerException {
        int size = getSize();
        if (size == 0) {
            return context.getZero();
        } else {
            GenericInteger result = context.getZero();
            for (Summand summand : summands) {
                result = result.add(summand.integerValue());
            }
            return result;
        }
    }

    @NotNull
    public Variable variableValue() throws NotVariableException {
        int size = getSize();
        if (size == 0) {
            throw new NotVariableException();
        } else if (size == 1) {
            final Summand s = this.summands.get(0);
            return s.variableValue();
        } else {
            throw new NotVariableException();
        }
    }

    @NotNull
    public List<Variable> variables() {
        return literalLcm().getVariables();
    }

/*    public static Variable[] variables(Generic elements[]) {
        final List<Variable> result = new ArrayList<Variable>();

        for (Generic element : elements) {
            for (Variable variable : element.variables()) {
                if (!result.contains(variable)) {
                    result.add(variable);
                }
            }
        }

        return ArrayUtils.toArray(result, new Variable[result.size()]);
    }*/

    public boolean isPolynomial(@NotNull Variable variable) {

        final Literal l = literalLcm();
        for (Variable lv : l.getVariables()) {
            if (!lv.isConstant(variable) && !lv.isIdentity(variable)) {
                return false;
            }
        }

        return true;
    }

    public boolean isConstant(@NotNull Variable variable) {

        final Literal l = literalLcm();
        for (Variable lv : l.getVariables()) {
            if (!lv.isConstant(variable)) {
                return false;
            }            
        }

        return true;
    }

/*    public JsclVector grad(Variable variable[]) {
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
    }*/

    public int compareTo(@NotNull Expression that) {
        return ExpressionComparator.instance.compare(this, that);
    }

    public int compareTo(@NotNull Generic generic) {
        if (generic instanceof Expression) {
            return compareTo((Expression) generic);
        } else if (generic instanceof GenericInteger || generic instanceof Rational || generic instanceof GenericNumeric) {
            return compareTo(newInstance(generic));
        } else {
            return generic.newInstance(this).compareTo(generic);
        }
    }

/*    @NotNull
    public static Expression valueOf(@NotNull Variable variable) {
        return valueOf(Literal.newI(variable));
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
        literal.init(new DoubleVariable(new GenericNumeric(Real.valueOf(value))), 1);
        expression.init(literal, GenericInteger.ONE);
        return expression;
    }*/

/*    public static Expression valueOf(@NotNull String expression) throws ParseException {
        final MutableInt position = new MutableInt(0);

        final Parser.Parameters p = Parser.Parameters.newInstance(expression, position, JsclMathEngine.instance);

        final Generic generic = ExpressionParser.parser.parse(p, null);

        ParserUtils.skipWhitespaces(p);

        int index = position.intValue();
        if (index < expression.length()) {
            throw new ParseException(Messages.msg_1, index, expression, index + 1);
        }

        return new Expression().newInstance(generic);
    }*/


    public String toString() {
        final StringBuilder result = new StringBuilder();

        if (signum() == 0) {
            result.append("0");
        }

        // result = coef[0] * literal[0] + coef[1] * literal[1] + ... +
        boolean first = true;
        for (Summand summand : summands) {            
            final Literal literal = summand.getLiteral();
            final GenericInteger coefficient = summand.getCoefficient();

            if (coefficient.signum() > 0 && first) {
                result.append("+");
            }

            if (literal.getDegree() == 0) {
                result.append(coefficient);
            } else {
                if (coefficient.abs().compareTo(context.getOne()) == 0) {
                    if (coefficient.signum() < 0) {
                        result.append("-");
                    }
                } else {
                    result.append(coefficient).append("*");
                }
                result.append(literal);
            }
            
            first = false;
        }

        return result.toString();
    }

    public String toJava() {
        final StringBuilder result = new StringBuilder();
        if (signum() == 0) {
            result.append("JsclDouble.valueOf(0)");
        }

        boolean first = true;
        for (Summand summand : summands) {
            Literal l = summand.getLiteral();
            GenericInteger en = summand.getCoefficient();
            if (first) {
                if (en.signum() < 0) {
                    result.append(".subtract(");
                    en = (GenericInteger) en.negate();
                } else result.append(".add(");
            }
            if (l.getDegree() == 0) result.append(en.toJava());
            else {
                if (en.abs().compareTo(context.getOne()) == 0) {
                    if (en.signum() > 0) result.append(l.toJava());
                    else if (en.signum() < 0) result.append(l.toJava()).append(".negate()");
                } else result.append(en.toJava()).append(".multiply(").append(l.toJava()).append(")");
            }
            if (first) result.append(")");
            
            first = false;
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
        
        boolean first = true;
        for (Summand summand : summands) {
            Literal l = summand.getLiteral();
            GenericInteger en = summand.getCoefficient();
            if (en.signum() > 0 && !first) {
                MathML e2 = element.newElement("mo");
                e2.appendChild(element.newText("+"));
                e1.appendChild(e2);
            }
            if (l.getDegree() == 0) separateSign(e1, en);
            else {
                if (en.abs().compareTo(context.getOne()) == 0) {
                    if (en.signum() < 0) {
                        MathML e2 = element.newElement("mo");
                        e2.appendChild(element.newText("-"));
                        e1.appendChild(e2);
                    }
                } else separateSign(e1, en);
                l.toMathML(e1, null);
            }
            
            first = false;
        }
        element.appendChild(e1);
    }

    @Override
    public boolean isZero() {
        // todo serso: check
        if ( getSize() == 0 ) {
            return true;
        } else {
            for (Summand summand : summands) {
                if ( !summand.isZero() ) {
                    return false;
                }
            }        
            return true;
        }        
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

}
