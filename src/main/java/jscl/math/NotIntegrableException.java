package jscl.math;

import jscl.AbstractJsclArithmeticException;
import org.solovyev.common.msg.Message;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 4:41 PM
 */
public class NotIntegrableException extends AbstractJsclArithmeticException {

    public NotIntegrableException(@org.jetbrains.annotations.NotNull String messageCode, Object... parameters) {
        super(messageCode, parameters);
    }

    public NotIntegrableException(@org.jetbrains.annotations.NotNull Message message) {
        super(message);
    }
}
