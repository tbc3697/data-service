package pub.tbc.data.job.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author tbc by 2020-10-25
 */
@Getter
@AllArgsConstructor
public enum SyncTypeEnum {

    /**
     *
     */
    AGENT_INFO,

    USER_INFO,
    USER_SETTINGS,

    VIRTUAL_CAPITAL_OPERATION,
    USER_VIRTUAL_WALLET,

    OTC_USER_BILL,
    OTC_USER_BALANCE,

    RISK_CONTRACT_GRANT,

    ASSETS_TRANSFER_INFO,

    CONTRACT_ACCOUNT,

    CONTRACT_DELEGATE_DEAL

    ;
}
