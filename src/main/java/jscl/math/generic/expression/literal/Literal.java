package jscl.math.generic.expression.literal;

import jscl.ImmutableObjectBuilder;
import jscl.ToJavaWritable;
import jscl.ToMathMlWritable;
import jscl.math.NotDivisibleException;
import jscl.math.Transformable;
import jscl.math.Variable;
import jscl.math.generic.Generic;
import jscl.math.generic.NotProductException;
import jscl.math.generic.NotVariableException;
import jscl.mathml.MathML;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Converter;

import java.util.*;

/**
 * Literal = ( var[0] ^ power[0] ) * ( var[1] ^ power[1] ) * ...
 */
public class Literal implements Comparable, ToMathMlWritable, ToJavaWritable, Iterable<Productand> {

    private static final Literal EMPTY_LITERAL = new Literal(Collections.<Productand>emptyList());

    @NotNull
    private final List<Productand> productands;

    @NotNull
    private final List<Variable> variables;

    private final int degree;

    @Override
    public Iterator<Productand> iterator() {
        return productands.iterator();
    }

    public Variable variableValue() {
        int size = getSize();
        if (size == 0) {
            throw new NotVariableException();
        } else if (size == 1) {
            final Productand p = productands.get(0);
            return p.variableValue();
        } else {
            throw new NotVariableException();
        }
    }




    /*
   * *********************************************************************
   *
   *                         CONSTRUCTORS
   *
   * *********************************************************************
    */

    public static class Builder extends ImmutableObjectBuilder<Literal> {

        private final List<Productand> productands;

        public Builder(int initialCapacity) {
            this.productands = new ArrayList<Productand>(initialCapacity);
        }

        public void addProductand(@NotNull Productand p) {
            if (isLocked()) {
                throw new IllegalStateException("Cannot add productand to already created expression!");
            }
            this.productands.add(p);
        }

        public void addProductand(@NotNull Variable variable, int exponent) {
            addProductand(Productand.newInstance(variable, exponent));
        }

        public void addProductand(@NotNull Variable variable) {
            addProductand(Productand.newInstance(variable));
        }

        @NotNull
        public Literal build0() {
            // avoid not used memory
            if (productands instanceof ArrayList) {
                ((ArrayList) productands).trimToSize();
            }

            return new Literal(productands);
        }
    }

    @NotNull
    public static Literal newEmpty() {
        return EMPTY_LITERAL;
    }

    @NotNull
    public static Literal newInstance(@NotNull Variable v) {
        final Builder b = new Builder(1);
        b.addProductand(v);
        return b.build();
    }

    private Literal(@NotNull List<Productand> productands) {
        this.productands = productands;
        this.variables = new ArrayList<Variable>(productands.size());

        int degree = 0;
        for (Productand productand : productands) {
            degree += productand.getExponent();
            variables.add(productand.getVariable());
        }

        this.degree = degree;
    }

    /*
   * *********************************************************************
   *
   *                         GETTERS
   *
   * *********************************************************************
    */

    @NotNull
    public Productand getProductand(int i) {
        return productands.get(i);
    }

    @NotNull
    public List<Variable> getVariables() {
        return Collections.unmodifiableList(variables);
    }

    @NotNull
    public List<Productand> getProductands() {
        return Collections.unmodifiableList(productands);
    }

    public int getSize() {
        return this.productands.size();
    }

    @NotNull
    public Literal multiply(@NotNull Literal that) {
        return LiteralMultiplicator.instance.multiply(this, that);
    }

    @NotNull
    public Literal divide(@NotNull Literal that) throws NotDivisibleException {
        return LiteralDivider.instance.divide(this, that);
    }

    @NotNull
    public Literal gcd(@NotNull Literal that) {
        return LiteralGcd.instance.gcd(this, that);
    }

    @NotNull
    public Literal lcm(@NotNull Literal that) {
        return LiteralLcm.instance.lcm(this, that);
    }

    @NotNull
    public List<Generic> productValue() throws NotProductException {
        final List<Generic> result = new ArrayList<Generic>(getSize());

        for (Productand productand : productands) {
            result.add(productand.asGeneric());
        }

        return result;
    }
    
/*	

	public Power powerValue() throws NotPowerException {
		if (size == 0) return new Power(JsclInteger.valueOf(1), 1);
		else if (size == 1) {
			Variable v = variables[0];
			int c = powers[0];
			return new Power(v.expressionValue(), c);
		} else throw new NotPowerException();
	}

	public Variable variableValue() throws NotVariableException {
		if (size == 0) throw new NotVariableException();
		else if (size == 1) {
			Variable v = variables[0];
			int c = powers[0];
			if (c == 1) return v;
			else throw new NotVariableException();
		} else throw new NotVariableException();
	}

	public Variable[] variables() {
		Variable va[] = new Variable[size];
		System.arraycopy(variables, 0, va, 0, size);
		return va;
	}*/

    public int getDegree() {
        return degree;
    }

    public int compareTo(@NotNull Literal that) {
        return LiteralComparator.instance.compare(this, that);
    }

    public int compareTo(Object o) {
        return compareTo((Literal) o);
    }
/*

	public static Literal newInstance() {
		return new Literal(0);
	}

	public static Literal valueOf(Variable variable) {
		return valueOf(variable, 1);
	}

	public static Literal valueOf(Variable variable, int power) {
		Literal l = new Literal(productands);
		l.init(variable, power);
		return l;
	}
*/

/*	void init(Variable var, int pow) {
		if (pow != 0) {
			init(1);
			variables[0] = var;
			powers[0] = pow;
			degree = pow;
		} else init(0);
	}

	public static Literal valueOf(Monomial monomial) {
		Literal l = new Literal(productands);
		l.init(monomial);
		return l;
	}

	void init(Monomial monomial) {
		Map map = new TreeMap();
		Variable unk[] = monomial.unknown();
		for (int i = 0; i < unk.length; i++) {
			int c = monomial.element(i);
			if (c > 0) map.put(unk[i], c);
		}
		init(map.size());
		Iterator it = map.entrySet().iterator();
		for (int i = 0; it.hasNext(); i++) {
			Map.Entry e = (Map.Entry) it.next();
			Variable v = (Variable) e.getKey();
			int c = (Integer) e.getValue();
			variables[i] = v;
			powers[i] = c;
			degree += c;
		}
	}*/

    @NotNull
    public Map<Variable, Generic> content(@NotNull Converter<Transformable, Generic> c) {
        final Map<Variable, Generic> result = new TreeMap<Variable, Generic>();

        for (Productand productand : productands) {
            final Variable variable = productand.getVariable();
            if (!result.containsKey(variable)) {
                result.put(variable, c.convert(variable));
            }
        }

        return result;
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();

        if (degree == 0) {
            result.append("1");
        } else {
            // result = var[0] ^ power[0] * var[1] ^ power[1]* ...
            for (int i = 0; i < getSize(); i++) {
                if (i > 0) {
                    result.append("*");
                }

                final Productand productand = getProductand(i);
                final int exponent = productand.getExponent();
                final Variable v = productand.getVariable();

                if (exponent == 1) {
                    result.append(v);
                } else {
                    // todo serso: uncomment code below if no additional brackets are needed
                    //if (v instanceof Fraction || v instanceof Pow) {
                    result.append("(").append(v).append(")");
                    //} else {
                    //	result.append(v);
                    //}
                    result.append("^").append(exponent);
                }
            }
        }

        return result.toString();
    }

    @Override
    @NotNull
    public String toJava() {
        final StringBuilder result = new StringBuilder();
        if (degree == 0) {
            result.append("JsclDouble.valueOf(1)");
        } else {
            for (int i = 0; i < getSize(); i++) {
                if (i > 0) {
                    result.append(".multiply(");
                }

                final Productand productand = getProductand(i);
                final int exponent = productand.getExponent();
                final Variable v = productand.getVariable();

                result.append(v.toJava());
                if (exponent != 1) {
                    result.append(".pow(").append(exponent).append(")");
                }

                if (i > 0) {
                    result.append(")");
                }
            }
        }
        return result.toString();
    }

    @Override
    public void toMathML(@NotNull MathML parent, @Nullable Object data) {
        if (degree == 0) {
            final MathML child = parent.newElement("mn");
            child.appendChild(parent.newElement("1"));
            parent.appendChild(child);
        } else {
            for (int i = 0; i < getSize(); i++) {
                final Productand productand = getProductand(i);
                productand.getVariable().toMathML(parent, productand.getExponent());
            }
        }

    }
}
