package com.group2.catanbackend.websocket.broker;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class WebSocketBrokerController {
    @MessageMapping("/hello")
    @SendTo("/topic/hello-response")
    public String handleHello(String message){
        System.out.println(message);
        return "echo from broker: " + HtmlUtils.htmlEscape(message);
    }
}
