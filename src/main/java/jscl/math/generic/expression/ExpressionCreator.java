package jscl.math.generic.expression;

import jscl.math.generic.Generic;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 9:22 PM
 */
public interface ExpressionCreator {

	@NotNull
	Expression newExpression(@NotNull Generic generic);
}
