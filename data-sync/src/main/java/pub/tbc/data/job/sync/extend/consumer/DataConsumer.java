package pub.tbc.data.job.sync.extend.consumer;

import java.util.List;
import java.util.function.Consumer;

/**
 * @Author tbc by 2020-10-25
 */
public interface DataConsumer<D> extends Consumer<List<D>> {

}
