package com.ting.gateway.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.TreeMap;

/**
 * 一致性hash
 *
 * @author ting
 * @version 1.0
 * @date 2021/12/7
 */
@Component
public class ConsistencyHashUtil {

    /**
     * 虚拟节点个数
     */
    private static final int VIRTUAL_NODES_NUMBER = 50;

    /**
     * hash环
     */
    private static final TreeMap<Integer, String> HASH_RING = new TreeMap<>();


    /**
     * 获取hash环
     *
     * @param clientList 客户端ip+端口集合
     * @return hash环
     */
    public static TreeMap<Integer, String> getHashRing(List<String> clientList) {
        if (CollectionUtils.isEmpty(clientList)) {
            for (String client : clientList) {
                HASH_RING.put(hash(client), client);
                for (int num = 0; num < VIRTUAL_NODES_NUMBER; num++) {
                    HASH_RING.put(hash(client + num), client);
                }
            }
        }
        return HASH_RING;
    }


    /**
     * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
     *
     * @param parameter 需要hash的参数
     * @return hash值
     */
    public static Integer hash(String parameter) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < parameter.length(); i++) {
            hash = (hash ^ parameter.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);

        }
        return hash;
    }


}
