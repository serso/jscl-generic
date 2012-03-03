package jscl.math.generic;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/19/12
 * Time: 12:12 AM
 */
public class DivideAndRemainderResult<G extends Generic> {

    @NotNull
    private final G divisionResult;

    @NotNull
    private final Generic remainder;

    private DivideAndRemainderResult(@NotNull G divisionResult, @NotNull Generic remainder) {
        this.divisionResult = divisionResult;
        this.remainder = remainder;
    }

    @NotNull
    public static <G extends Generic> DivideAndRemainderResult<G> newInstance(@NotNull G divisionResult, @NotNull Generic remainder) {
        return new DivideAndRemainderResult<G>(divisionResult, remainder);
    }

    @NotNull
    public G getDivisionResult() {
        return divisionResult;
    }

    @NotNull
    public Generic getRemainder() {
        return remainder;
    }
}
