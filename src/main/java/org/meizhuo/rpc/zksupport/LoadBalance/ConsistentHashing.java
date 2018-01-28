package org.meizhuo.rpc.zksupport.LoadBalance;

import org.meizhuo.rpc.Exception.ProvidersNoFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by wephone on 18-1-28.
 * 一致性hash负载均衡策略
 */
public class ConsistentHashing implements LoadBalance{

    //每个服务用红黑树维护有序的一致性Hash环
    private static Map<String,SortedMap<Integer,String>> sortedServersMap=new HashMap<>();

    private String consumerIP;
    //虚拟节点数 默认16个虚拟节点
    private Integer virtualNodeNums=16;


    //FNV1_32_HASH Hash算法
    private Integer getHash(String ip){
        final int p = 16777619;
        int hash = (int)2166136261L;
        for (int i = 0; i < ip.length(); i++){
            hash = (hash ^ ip.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值
        if (hash < 0){
            hash = Math.abs(hash);
        }
        return hash;
    }

    public void setVirtualNodeNums(Integer virtualNodeNums) {
        this.virtualNodeNums = virtualNodeNums;
    }

    public void setConsumerIP(String consumerIP) {
        this.consumerIP = consumerIP;
    }

    @Override
    public String chooseIP(String serviceName) throws ProvidersNoFoundException {
        SortedMap<Integer,String> serverRBTree=sortedServersMap.get(serviceName);
        Integer consumerHash=getHash(consumerIP);
        Integer key=consumerHash;
        if (!serverRBTree.containsKey(consumerHash)){
            // 得到大于该Hash值的所有Map
            SortedMap<Integer, String> tailMap=serverRBTree.tailMap(consumerHash);
            if (tailMap.isEmpty()){
                //如果没有比它大的 就取第一个
                key=serverRBTree.firstKey();
            }else {
                //取最接近的一个
                key=tailMap.firstKey();
            }
        }
        String ipNode=serverRBTree.get(key);
        String[] realIP=ipNode.split("-");
        return realIP[0];
    }
}
