package jscl.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 4:36 PM
 */
public final class TextUtils {

	private TextUtils() {
		throw new AssertionError();
	}

	@NotNull
	private static final Map<String, String> greekMappings = new HashMap<String, String>();

	static {
		greekMappings.put("Alpha", "\u0391");
		greekMappings.put("Beta", "\u0392");
		greekMappings.put("Gamma", "\u0393");
		greekMappings.put("Delta", "\u0394");
		greekMappings.put("Epsilon", "\u0395");
		greekMappings.put("Zeta", "\u0396");
		greekMappings.put("Eta", "\u0397");
		greekMappings.put("Theta", "\u0398");
		greekMappings.put("Iota", "\u0399");
		greekMappings.put("Kappa", "\u039A");
		greekMappings.put("Lambda", "\u039B");
		greekMappings.put("Mu", "\u039C");
		greekMappings.put("Nu", "\u039D");
		greekMappings.put("Xi", "\u039E");
		greekMappings.put("Pi", "\u03A0");
		greekMappings.put("Rho", "\u03A1");
		greekMappings.put("Sigma", "\u03A3");
		greekMappings.put("Tau", "\u03A4");
		greekMappings.put("Upsilon", "\u03A5");
		greekMappings.put("Phi", "\u03A6");
		greekMappings.put("Chi", "\u03A7");
		greekMappings.put("Psi", "\u03A8");
		greekMappings.put("Omega", "\u03A9");
		greekMappings.put("alpha", "\u03B1");
		greekMappings.put("beta", "\u03B2");
		greekMappings.put("gamma", "\u03B3");
		greekMappings.put("delta", "\u03B4");
		greekMappings.put("epsilon", "\u03B5");
		greekMappings.put("zeta", "\u03B6");
		greekMappings.put("eta", "\u03B7");
		greekMappings.put("theta", "\u03B8");
		greekMappings.put("iota", "\u03B9");
		greekMappings.put("kappa", "\u03BA");
		greekMappings.put("lambda", "\u03BB");
		greekMappings.put("mu", "\u03BC");
		greekMappings.put("nu", "\u03BD");
		greekMappings.put("xi", "\u03BE");
		greekMappings.put("pi", "\u03C0");
		greekMappings.put("rho", "\u03C1");
		greekMappings.put("sigma", "\u03C3");
		greekMappings.put("tau", "\u03C4");
		greekMappings.put("upsilon", "\u03C5");
		greekMappings.put("phi", "\u03C6");
		greekMappings.put("chi", "\u03C7");
		greekMappings.put("psi", "\u03C8");
		greekMappings.put("omega", "\u03C9");
		greekMappings.put("infin", "\u221E");
		greekMappings.put("nabla", "\u2207");
		greekMappings.put("aleph", "\u2135");
		greekMappings.put("hbar", "\u210F");
		greekMappings.put("hamilt", "\u210B");
		greekMappings.put("lagran", "\u2112");
		greekMappings.put("square", "\u25A1");
	}

	@NotNull
	public static Map<String, String> getGreekMappings() {
		return Collections.unmodifiableMap(greekMappings);
	}
}
