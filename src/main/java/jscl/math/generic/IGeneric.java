package jscl.math.generic;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 9:26 PM
 */
public interface IGeneric<G extends IGeneric<G>> {

    @NotNull
    G inverse();

    @NotNull
    G gcd(@NotNull G that);

    @NotNull
    G scm(@NotNull G that);

    int signum();

    @NotNull
    G pow(int exponent);

    @NotNull
    G abs();

    @NotNull
    G negate();

    @NotNull
    G valueOf(@NotNull G that);
}
