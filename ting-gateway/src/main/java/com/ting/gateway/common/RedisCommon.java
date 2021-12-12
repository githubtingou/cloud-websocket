package com.ting.gateway.common;

/**
 * redis 公共参数
 *
 * @author ting
 * @version 1.0
 * @date 2021/12/7
 */
public class RedisCommon {

    /**
     * hash环
     */
    public static final String RING_HASH_KEY = "ting:ring:hash";

    /**
     * 每个客户端的下的hash
     */
    public static final String RING_CLIENT_HASH_KEY_PREFIX = "ting:client:ring:hash:";

    /**
     * websocket服务
     */
    public static final String WEBSOCKET_KEY = "ting:websocket";

}
