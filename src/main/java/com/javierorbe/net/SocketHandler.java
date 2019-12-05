package com.javierorbe.net;

import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SocketHandler<T> implements ISocketHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketHandler.class);

    private boolean running = false;

    protected abstract Socket getSocket();

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void close() {
        setRunning(false);
        try {
            getSocket().close();
        } catch (IOException e) {
            LOGGER.error("Error closing socket handler.", e);
        }
    }
}
