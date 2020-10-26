//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖镇楼           BUG辟易
//
//                             佛曰:
//
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
// 

package pub.tbc.data.rest.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author tbc by 2020/8/7 4:47 下午
 */
@Slf4j
@ControllerAdvice
@Order(-101)
public class CustomExceptionHandler {


    // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    // @ExceptionHandler(Exception.class)
    // @ResponseBody
    // public R exceptionHandle(Exception e) {
    //     String errMsg = getOrDefault(e.getMessage(), R.DEFAULT_SERVER_ERR_MSG);
    //     errLog(e);
    //     log.error("业务异常=> [{}]", e.getMessage(), e);
    //     return R.serverErr(errMsg);
    // }

    // @ExceptionHandler(RuntimeException.class)
    // @ResponseBody
    // public R runtimeExceptionHandle(RuntimeException e) {
    //     String errMsg = getOrDefault(e.getMessage(), R.DEFAULT_SERVER_ERR_MSG);
    //     errLog(e);
    //     log.error("业务异常=> [{}]", e.getMessage(), e);
    //     return R.serverErr(errMsg);
    // }

}
