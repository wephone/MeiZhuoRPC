package trace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


public class StartCallImpl implements StartCall {

    @Autowired
    Second second;

    @Override
    public Integer callSecond() {
        System.out.println("中转站接收成功 发出调用请求");
        return second.callThird();
    }
}
