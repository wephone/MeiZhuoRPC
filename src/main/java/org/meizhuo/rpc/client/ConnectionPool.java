package org.meizhuo.rpc.client;

import io.netty.channel.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ConnectionPool {

    private GenericObjectPool pool;

    public ConnectionPool(String ip,Integer port) {
        ConnectFactory connectFactory=new ConnectFactory(ip, port);
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        //最大空闲连接数
        config.setMaxIdle(2);
        //最大连接数
        config.setMaxTotal(4);
        pool=new GenericObjectPool(connectFactory,config);
    }

    public Channel getChannel() throws Exception {
        return (Channel) pool.borrowObject();
    }

    public void releaseChannel(Channel channel){
        pool.returnObject(channel);
    }

    public void destoryChannel(){
        pool.close();
    }

}
