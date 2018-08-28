package trace;

public class SecondImpl implements Second {

    @Override
    public Integer callThird() {
        System.out.println("最后一战接收成功 返回200");
        return 200;
    }
}
