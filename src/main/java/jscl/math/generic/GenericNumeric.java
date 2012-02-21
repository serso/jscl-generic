package jscl.math.generic;

import jscl.math.ArithmeticOperationException;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.mathml.MathML;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * User: serso
 * Date: 2/21/12
 * Time: 12:57 PM
 */
public class GenericNumeric extends Generic{
    @NotNull
    @Override
    protected Generic gcd() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Generic antiDerivative(@NotNull Variable variable) throws NotIntegrableException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Generic derivative(@NotNull Variable variable) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Generic substitute(@NotNull Variable variable, Generic generic) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Generic newInstance(Generic generic) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Generic[] sumValue() {
        return new Generic[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Set<Variable> variables() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPolynomial(@NotNull Variable variable) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isConstant(@NotNull Variable variable) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int compareTo(Generic generic) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toJava() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void toMathML(MathML element, @Nullable Object data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isZero() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Generic add(@NotNull Generic generic) throws ArithmeticOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Generic multiply(@NotNull Generic generic) throws ArithmeticOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Generic divide(@NotNull Generic generic) throws ArithmeticOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Generic gcd(@NotNull Generic that) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int signum() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Generic negate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int degree() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Generic valueOf(@NotNull Generic that) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Generic expand() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Generic factorize() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Generic elementary() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Generic simplify() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public Generic numeric() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
