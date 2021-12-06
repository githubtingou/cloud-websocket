package com.ting.websocket.dto;

import lombok.Data;

/**
 * 消息参数
 *
 * @author ting
 * @version 1.0
 * @date 2021/11/20
 */
@Data
public class MsgDTO {

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 是否群发
     */
    private boolean isAllSend;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 发送人
     */
    private String sendUser;

    /**
     * 接受人
     */
    private String receiveUser;

}
