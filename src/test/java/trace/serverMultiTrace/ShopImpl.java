package trace.serverMultiTrace;

import org.springframework.beans.factory.annotation.Autowired;

public class ShopImpl implements ShopInterface {

    @Autowired
    FactoryInterface factory;

    @Override
    public String buyFood() {
        System.out.println("处理原料"+factory.getMaterial());
        return "食物";
    }
}
