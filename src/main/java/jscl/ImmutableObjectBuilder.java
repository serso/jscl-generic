package jscl;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 2/19/12
 * Time: 12:50 AM
 */
public abstract class ImmutableObjectBuilder<T> implements Builder<T> {

	private boolean locked = false;

	@NotNull
	public final T build() {
		this.locked = true;
		return build0();
	}

	public boolean isLocked() {
		return locked;
	}

	@NotNull
	protected abstract T build0();
}
