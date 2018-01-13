package org.meizhuo.rpc.Exception;

/**
 * Created by wephone on 18-1-13.
 */
public class ProvidersNoFoundException extends Exception {

    public ProvidersNoFoundException() {
        super("MeiZhuoRPC could not found any available providers");
    }
}
