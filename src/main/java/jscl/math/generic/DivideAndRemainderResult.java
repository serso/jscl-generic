package jscl.math.generic;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/19/12
 * Time: 12:12 AM
 */
public class DivideAndRemainderResult {

    @NotNull
    private final Generic divisionResult;

    @NotNull
    private final Generic remainder;

    private DivideAndRemainderResult(@NotNull Generic divisionResult, @NotNull Generic remainder) {
        this.divisionResult = divisionResult;
        this.remainder = remainder;
    }

    public static DivideAndRemainderResult newInstance(@NotNull Generic divisionResult, @NotNull Generic remainder) {
        return new DivideAndRemainderResult(divisionResult, remainder);
    }

    @NotNull
    public Generic getDivisionResult() {
        return divisionResult;
    }

    @NotNull
    public Generic getRemainder() {
        return remainder;
    }
}
