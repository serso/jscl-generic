package jscl.math;

import java.util.Comparator;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 4:42 PM
 */
class VariableComparator implements Comparator<Variable> {

    public static final Comparator<Variable> instance = new VariableComparator();

    private VariableComparator() {
    }

    public int compare(Variable o1, Variable o2) {
        return value(o1) - value(o2);
    }

    static int value(Variable v) {
        int n;
        /*if (v instanceof TechnicalVariable) n = 0;
          else if (v instanceof IntegerVariable) n = 1;
          else if (v instanceof DoubleVariable) n = 2;
          else if (v instanceof Fraction && ((Fraction) v).integer()) n = 3;
          else if (v instanceof Sqrt && ((Sqrt) v).imaginary()) n = 4;
          else if (v instanceof Constant) n = 5;
          else if (v instanceof Root) n = 6;
          else if (v instanceof Algebraic) n = 7;
          else if (v instanceof ImplicitFunction) n = 8;
          else if (v instanceof Function) n = 9;
          else if (v instanceof Factorial) n = 10;
          else if (v instanceof Operator) n = 11;
          else if (v instanceof ExpressionVariable) n = 12;
          else if (v instanceof VectorVariable) n = 13;
          else if (v instanceof MatrixVariable) n = 14;
          else */
        throw new ArithmeticException("Forget to add compare object of type: " + v.getClass());
        //return n;
    }
}
