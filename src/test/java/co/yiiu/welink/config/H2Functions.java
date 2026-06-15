package co.yiiu.welink.config;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * H2 自定义函数，用于在 H2 内存数据库中模拟 MySQL 的 date_add() 和 date_sub() 函数。
 * <p>
 * MySQL 语法:
 *   date_add(datetime, INTERVAL n unit)
 *   date_sub(datetime, INTERVAL n unit)
 * <p>
 * 通过 CREATE ALIAS 注册到 H2 中，确保测试时的 SQL 兼容性。
 */
public class H2Functions {

    private H2Functions() {
    }

    /**
     * 模拟 MySQL 的 DATE_ADD 函数
     *
     * @param ts           原始时间戳
     * @param intervalExpr 时间间隔表达式，格式: "INTERVAL n unit"
     * @return 增加后的时间戳
     */
    public static Timestamp dateAdd(Timestamp ts, String intervalExpr) {
        return addInterval(ts, intervalExpr, 1);
    }

    /**
     * 模拟 MySQL 的 DATE_SUB 函数
     *
     * @param ts           原始时间戳
     * @param intervalExpr 时间间隔表达式，格式: "INTERVAL n unit"
     * @return 减少后的时间戳
     */
    public static Timestamp dateSub(Timestamp ts, String intervalExpr) {
        return addInterval(ts, intervalExpr, -1);
    }

    private static Timestamp addInterval(Timestamp ts, String intervalExpr, int sign) {
        try {
            String trimmed = intervalExpr.trim();
            String[] parts = trimmed.split("\\s+");
            if (parts.length < 3 || !"INTERVAL".equalsIgnoreCase(parts[0])) {
                return ts;
            }
            int amount = Integer.parseInt(parts[1]) * sign;
            String unit = parts[2].toUpperCase();
            LocalDateTime ldt = ts.toLocalDateTime();
            switch (unit) {
                case "DAY":
                    ldt = ldt.plusDays(amount);
                    break;
                case "HOUR":
                    ldt = ldt.plusHours(amount);
                    break;
                case "MINUTE":
                    ldt = ldt.plusMinutes(amount);
                    break;
                case "MONTH":
                    ldt = ldt.plusMonths(amount);
                    break;
                case "YEAR":
                    ldt = ldt.plusYears(amount);
                    break;
                case "SECOND":
                    ldt = ldt.plusSeconds(amount);
                    break;
                default:
                    break;
            }
            return Timestamp.valueOf(ldt);
        } catch (Exception e) {
            return ts;
        }
    }
}
