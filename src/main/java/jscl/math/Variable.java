package jscl.math;

import jscl.math.generic.Generic;
import jscl.mathml.MathML;
import jscl.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.utils.Converter;
import org.solovyev.common.utils.StringUtils;

import java.util.*;

public abstract class Variable implements Comparable, MathEntity {

	@NotNull
	public static final Comparator<Variable> comparator = VariableComparator.instance;

	private Integer id;

	@NotNull
	protected String name;

	private boolean system = true;

	public Variable(@NotNull String name) {
		this.name = name;
	}

	@NotNull
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public boolean isIdDefined() {
		return id != null;
	}

	public void setId(@NotNull Integer id) {
		this.id = id;
	}

	@NotNull
	public final String getName() {
		return name;
	}

	public boolean isSystem() {
		return system;
	}

	protected void setSystem(boolean system) {
		this.system = system;
	}

	public void copy(@NotNull MathEntity that) {
		if (that instanceof Variable) {
			this.name = ((Variable) that).name;
		}
	}

	public abstract Generic antiDerivative(Variable variable) throws NotIntegrableException;

	@NotNull
	public abstract Generic derivative(Variable variable);

	public abstract Generic substitute(Variable variable, Generic generic);

	public abstract Generic expand();

	public abstract Generic factorize();

	public abstract Generic elementary();

	public abstract Generic simplify();

	public abstract Generic numeric();

/*	@NotNull
   public Expression expressionValue() {
	   return Expression.valueOf(this);
   }*/

	public abstract boolean isConstant(Variable variable);

	public boolean isIdentity(@NotNull Variable variable) {
		return this.compareTo(variable) == 0;
	}

	public abstract int compareTo(Variable variable);

	public int compareTo(Object o) {
		return compareTo((Variable) o);
	}

	public boolean equals(Object obj) {
		return obj instanceof Variable && compareTo((Variable) obj) == 0;
	}

/*	public static Variable valueOf(String str) throws ParseException, NotVariableException {
		return Expression.valueOf(str).variableValue();
	}

	public static Variable[] valueOf(String str[]) throws ParseException, NotVariableException {
		int n = str.length;
		Variable var[] = new Variable[n];
		for (int i = 0; i < n; i++) var[i] = valueOf(str[i]);
		return var;
	}*/

	public String toString() {
		return name;
	}

	public String toJava() {
		return name;
	}

	public void toMathML(@NotNull MathML parent, @Nullable Object data) {
		int exponent = data instanceof Integer ? (Integer) data : 1;
		if (exponent == 1) {
			nameToMathML(parent);
		} else {
			final MathML e1 = parent.newElement("msup");
			nameToMathML(e1);
			final MathML e2 = parent.newElement("mn");
			e2.appendChild(parent.newText(String.valueOf(exponent)));
			e1.appendChild(e2);
			parent.appendChild(e1);
		}
	}

	private void nameToMathML(@NotNull MathML element) {
		final MathML e1 = element.newElement("mi");
		e1.appendChild(element.newText(StringUtils.getNotEmpty(TextUtils.getGreekMappings().get(name), name)));
		element.appendChild(e1);
	}

	@NotNull
	public abstract Variable newInstance();
/*
	@NotNull
	public abstract Set<? extends Constant> getConstants();*/

	protected static final Converter<Generic, Generic> FACTORIZE_CONVERTER = new Converter<Generic, Generic>() {
		@NotNull
		@Override
		public Generic convert(@NotNull Generic generic) {
			return generic.factorize();
		}
	};

	protected static final Converter<Generic, Generic> ELEMENTARY_CONVERTER = new Converter<Generic, Generic>() {
		@NotNull
		@Override
		public Generic convert(@NotNull Generic generic) {
			return generic.elementary();
		}
	};

	protected static final Converter<Generic, Generic> EXPAND_CONVERTER = new Converter<Generic, Generic>() {
		@NotNull
		@Override
		public Generic convert(@NotNull Generic generic) {
			return generic.expand();
		}
	};

	protected static final Converter<Generic, Generic> NUMERIC_CONVERTER = new Converter<Generic, Generic>() {
		@NotNull
		@Override
		public Generic convert(@NotNull Generic generic) {
			return generic.numeric();
		}
	};
}

