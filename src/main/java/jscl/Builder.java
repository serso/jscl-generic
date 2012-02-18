package jscl;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/19/12
 * Time: 12:50 AM
 */
public interface Builder<T> {

	@NotNull
	public T build ();
}
