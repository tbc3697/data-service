package pub.tbc.data.job.sync;

/**
 * 主键提供者
 *
 * @Author tbc by 2020-10-25
 */
public interface PrimaryProvider<C> {

    C primary();
}
