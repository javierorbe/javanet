package com.javierorbe.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSocketServer<T> implements ISocketServer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSocketServer.class);

    private boolean running = false;
    private final Collection<ISocketHandler<T>> clients = Collections.synchronizedSet(new HashSet<>());

    protected abstract ServerSocket getServerSocket();

    public boolean isRunning() {
        return running;
    }

    protected void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void close() {
        setRunning(false);
        clients.forEach(ISocketHandler::close);

        try {
            getServerSocket().close();
        } catch (IOException e) {
            LOGGER.error("Error closing socket server.");
        }
    }

    @Override
    public Future<?> broadcast(T t) {
        return Executors.newSingleThreadExecutor()
                .submit(() -> clients.forEach(client -> client.send(t)));
    }

    protected void addClient(ISocketHandler<T> handler) {
        clients.add(handler);
    }

    protected void removeClient(ISocketHandler<T> handler) {
        clients.remove(handler);
    }
}
