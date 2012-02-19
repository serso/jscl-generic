package jscl;

import jscl.mathml.MathML;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 1:31 AM
 */
public interface ToMathMlWritable {

	void toMathML(@NotNull MathML parent, @Nullable Object data);
}
