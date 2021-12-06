package com.ting.websocket.controller;

import com.ting.websocket.dto.MsgDTO;
import com.ting.websocket.socket.handle.WebSocketHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * chat
 *
 * @author ting
 * @version 1.0
 * @date 2021/11/20
 */
@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    private final WebSocketHandle webSocketHandle;

    public ChatController(WebSocketHandle webSocketHandle) {
        this.webSocketHandle = webSocketHandle;
    }

    @PostMapping(value = "sendMsg")
    public String sendMsg(@RequestBody MsgDTO msgDTO) {
        try {
            return webSocketHandle.sendMeg(msgDTO);
        } catch (Exception e) {
            log.error("消息发送失败:", e);
        }
        return "消息发送失败";
    }

}
