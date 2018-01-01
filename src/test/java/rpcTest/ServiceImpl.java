package rpcTest;

/**
 * Created by wephone on 18-1-1.
 */
public class ServiceImpl implements Service {
    @Override
    public Double remoteService(Double arg1, String arg2) {
        return 1234567.0;
    }
}
