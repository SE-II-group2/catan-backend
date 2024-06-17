package com.group2.catanbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class SocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final UserHandshakeHandler handshakeHandler;
    private final GameChannelInterceptor gameChannelInterceptor;
    private TaskScheduler messageBrokerTaskScheduler;

    private final AuthHandshakeInterceptor authHandshakeInterceptor;
    public SocketConfiguration(@Autowired UserHandshakeHandler handshakeHandler,
                               @Autowired GameChannelInterceptor gameChannelInterceptor,
                               @Autowired AuthHandshakeInterceptor authHandshakeInterseptor){
        this.handshakeHandler = handshakeHandler;
        this.gameChannelInterceptor = gameChannelInterceptor;
        this.authHandshakeInterceptor = authHandshakeInterseptor;
    }
    @Autowired
    public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler taskScheduler) {
        this.messageBrokerTaskScheduler = taskScheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(Constants.SOCKET_ENDPOINT).setAllowedOrigins("*")
                .setHandshakeHandler(handshakeHandler)
                .addInterceptors(authHandshakeInterceptor);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(gameChannelInterceptor);
    }

    //TODO: Possibility to detect disconnections and disconnect from server.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(this.messageBrokerTaskScheduler);
        registry.setUserDestinationPrefix(Constants.USER_DESTINATION_PREFIX);
        registry.setApplicationDestinationPrefixes("/app");

    }
}
