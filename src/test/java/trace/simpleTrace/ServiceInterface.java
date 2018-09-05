package trace.simpleTrace;

/**
 * Created by wephone on 17-12-30.
 */
public interface ServiceInterface {

    Double remoteService(Double arg1, String arg2);
    int intMethodTest(int a);
    Integer IntegerMethodTest(Integer a);
    int intMethodIntegerArgsTest(Integer a);
    String stringMethodIntegerArgsTest(Integer a, Double b);
}
