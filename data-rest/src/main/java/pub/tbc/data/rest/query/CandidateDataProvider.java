package pub.tbc.data.rest.query;

/**
 * 候选数据提供者
 *
 * @Author tbc by 2020-10-25
 */
public interface CandidateDataProvider {

    /**
     * 获取候选数据
     *
     * @param params
     * @return
     */
    Object getCandidateData(Object params);

}
