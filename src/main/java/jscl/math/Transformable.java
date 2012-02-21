package jscl.math;

import jscl.math.generic.Generic;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.utils.Converter;

/**
 * User: serso
 * Date: 2/20/12
 * Time: 4:51 PM
 */
public interface Transformable {

    @NotNull
    Generic expand();

    @NotNull
    Generic factorize();

    @NotNull
    Generic elementary();

    @NotNull
    Generic simplify();

    @NotNull
    Generic numeric();

    public static final Converter<Transformable, Generic> FACTORIZE_CONVERTER = new Converter<Transformable, Generic>() {
        @NotNull
        @Override
        public Generic convert(@NotNull Transformable from) {
            return from.factorize();
        }
    };

    public static final Converter<Transformable, Generic> ELEMENTARY_CONVERTER = new Converter<Transformable, Generic>() {
        @NotNull
        @Override
        public Generic convert(@NotNull Transformable from) {
            return from.elementary();
        }
    };

    public static final Converter<Transformable, Generic> EXPAND_CONVERTER = new Converter<Transformable, Generic>() {
        @NotNull
        @Override
        public Generic convert(@NotNull Transformable from) {
            return from.expand();
        }
    };

    public static final Converter<Transformable, Generic> NUMERIC_CONVERTER = new Converter<Transformable, Generic>() {
        @NotNull
        @Override
        public Generic convert(@NotNull Transformable from) {
            return from.numeric();
        }
    };

    public static final Converter<Transformable, Generic> SIMPLIFY_CONVERTER = new Converter<Transformable, Generic>() {
        @NotNull
        @Override
        public Generic convert(@NotNull Transformable from) {
            return from.simplify();
        }
    };
}
