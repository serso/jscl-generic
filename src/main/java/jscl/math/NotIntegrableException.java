package jscl.math;

import jscl.AbstractJsclArithmeticException;
import jscl.math.generic.Generic;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 4:41 PM
 */
public class NotIntegrableException extends AbstractJsclArithmeticException {

    public NotIntegrableException(@NotNull Generic g, @NotNull Variable v) {
        super("not integrable");
    }

}
