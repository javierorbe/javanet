package com.javierorbe.net;

import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ObjectSocketClient<T> extends ObjectSocketHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectSocketClient.class);

    private final String hostname;
    private final int port;

    private Socket socket;

    protected ObjectSocketClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(hostname, port);
        } catch (IOException e) {
            LOGGER.error("Error creating socket.", e);
            return;
        }
        listen();
    }

    @Override
    protected Socket getSocket() {
        return socket;
    }
}
