package at.ac.tuwien.sepm.groupphase.backend.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SignalingHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignalingHandler.class);
    private static final String LOCALHOSTTOKEN = "$%&";
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
        throws IOException {
        for (WebSocketSession webSocketSession : this.sessions) {
            synchronized (webSocketSession) {
                if (webSocketSession.isOpen() && !session.getId().equals(webSocketSession.getId())) {
                    LOGGER.trace("Msg send: {}", message.getPayload());
                    webSocketSession.sendMessage(message);
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        this.sessions.remove(session);
    }
}
