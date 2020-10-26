package pub.tbc.data.rest.query;

import java.math.BigDecimal;

/**
 * @Author tbc by 2020-10-25
 */
public interface NumberCompare {

    default boolean eq(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) == 0;
    }

    default boolean gt(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > 0;
    }

    default boolean gl(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) < 0;
    }

    default boolean gte(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) >= 0;
    }

    default boolean gle(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) <= 0;
    }

}
