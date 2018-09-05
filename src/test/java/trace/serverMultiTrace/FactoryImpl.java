package trace.serverMultiTrace;

public class FactoryImpl implements FactoryInterface {
    @Override
    public String getMaterial() {
        System.out.println("发送原料小麦");
        return "小麦";
    }
}
