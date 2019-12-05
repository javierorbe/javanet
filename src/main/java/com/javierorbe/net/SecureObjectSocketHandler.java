package com.javierorbe.net;

import java.io.IOException;
import javax.net.ssl.SSLSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SecureObjectSocketHandler<T> extends ObjectSocketHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureObjectSocketHandler.class);

    @Override
    public void run() {
        try {
            getSocket().startHandshake();
        } catch (IOException e) {
            LOGGER.error("Handshake error.", e);
            return;
        }
        listen();
    }

    @Override
    protected abstract SSLSocket getSocket();
}
