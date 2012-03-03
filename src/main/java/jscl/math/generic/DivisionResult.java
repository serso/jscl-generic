package jscl.math.generic;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/19/12
 * Time: 12:12 AM
 */
public class DivisionResult<G extends Generic> {

    @NotNull
    private final G quotient;

    @NotNull
    private final G remainder;

    private DivisionResult(@NotNull G quotient, @NotNull G remainder) {
        this.quotient = quotient;
        this.remainder = remainder;
    }

    @NotNull
    public static <G extends Generic> DivisionResult<G> newInstance(@NotNull G quotient, @NotNull G remainder) {
        return new DivisionResult<G>(quotient, remainder);
    }

    @NotNull
    public G getQuotient() {
        return quotient;
    }

    @NotNull
    public G getRemainder() {
        return remainder;
    }
}
