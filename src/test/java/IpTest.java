import org.junit.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by wephone on 18-1-7.
 */
public class IpTest {


    public static void main(String[] args) throws UnknownHostException, SocketException {
//        String localIP = "";
//        InetAddress addr = (InetAddress) InetAddress.getLocalHost();
//        //获取本机IP
//        localIP = addr.getHostAddress().toString();
//        System.out.println(localIP);

        System.out.println("Host addr: " + InetAddress.getLocalHost().getHostAddress());  // often returns "127.0.0.1"
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        for (; n.hasMoreElements();)
        {
            NetworkInterface e = n.nextElement();
            System.out.println("Interface: " + e.getName());
            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements();)
            {
                InetAddress addr = a.nextElement();
                System.out.println("  " + addr.getHostAddress());
            }
        }
    }

}
