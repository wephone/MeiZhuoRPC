package rpcTest;

/**
 * Created by wephone on 18-1-13.
 */
public class Service2Impl implements Service2{
    @Override
    public String testString(Double a) {
        double b=a*2;
        return b+"";
    }
}
