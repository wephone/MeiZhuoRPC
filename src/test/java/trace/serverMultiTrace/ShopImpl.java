package trace.serverMultiTrace;

import org.springframework.beans.factory.annotation.Autowired;

public class ShopImpl implements ShopInterface {

    @Autowired
    FactoryInterface factory;

    @Override
    public String buyFood() {
        String str=factory.getMaterial();
        System.out.println("处理原料"+str);
        return "食物";
    }
}
