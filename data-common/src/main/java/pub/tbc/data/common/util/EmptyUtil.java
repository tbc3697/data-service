package pub.tbc.data.common.util;

import java.math.BigDecimal;
import java.util.function.Supplier;

/**
 * @Author tbc by 2020-10-25
 */
public class EmptyUtil {

    public static Integer nonNull(Integer data) {
        return nonNull(data, () -> Integer.valueOf(0));
    }

    public static Long nonNull(Long data) {
        return nonNull(data, () -> Long.valueOf(0L));
    }

    public static BigDecimal nonNull(BigDecimal data) {
        return nonNull(data, () -> BigDecimal.ZERO);
    }

    public static <T> T nonNull(T t, Supplier<T> supplier) {
        return t == null ? supplier.get() : t;
    }
}


