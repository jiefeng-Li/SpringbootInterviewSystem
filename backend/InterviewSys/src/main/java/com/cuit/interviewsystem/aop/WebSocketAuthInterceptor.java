package com.cuit.interviewsystem.aop;

import com.cuit.interviewsystem.utils.JWTUtil;
import jakarta.annotation.Resource;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Resource
    private JWTUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            // 获取请求头中的token
            String token = servletRequest.getServletRequest().getHeader("Authorization");

            // 验证token是否有效
            if (token != null && jwtUtil.verify(token)) {
                // 将用户ID存入WebSocket会话属性中
                String userId = jwtUtil.parse(token, JWTUtil.ELEMENT.USER_ID);
                attributes.put("userId", userId);
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // 握手成功后的处理，可以留空
    }
}
