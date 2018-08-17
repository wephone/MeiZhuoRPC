package org.meizhuo.rpc.client;

import io.netty.channel.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.meizhuo.rpc.core.RPC;

public class ConnectionPool {

    private GenericObjectPool pool;
    private String fullIp;

    public ConnectionPool(String ip,Integer port) {
        ConnectFactory connectFactory=new ConnectFactory(ip, port);
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        //最大空闲连接数
        config.setMaxIdle(RPC.getClientConfig().getPoolMaxIdle());
        //最大连接数
        config.setMaxTotal(RPC.getClientConfig().getPoolMaxTotal());
        pool=new GenericObjectPool(connectFactory,config);
        fullIp=ip+":"+port;
    }

    public Channel getChannel() throws Exception {
        return (Channel) pool.borrowObject();
    }

    public void releaseChannel(Channel channel){
        pool.returnObject(channel);
    }

    public void destroyChannel(){
        //关闭Netty线程资源及其注册的连接
        ((ConnectFactory)pool.getFactory()).getGroup().shutdownGracefully();
        pool.close();
        //移除引用
        RPCRequestNet.getInstance().connectionPoolMap.remove(fullIp);
    }

}
