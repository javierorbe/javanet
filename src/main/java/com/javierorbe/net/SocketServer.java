package com.javierorbe.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Socket server. */
public abstract class SocketServer<T> extends AbstractSocketServer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSocket.class);

    private final int port;
    private ServerSocket serverSocket;

    protected SocketServer(int port) {
        this.port = port;
    }

    @Override
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LOGGER.error("Error creating server socket.", e);
            return;
        }

        LOGGER.info("Server started listening on port {}.", port);
        listen();
    }

    private void listen() {
        setRunning(true);

        while (isRunning()) {
            try {
                Socket socket = serverSocket.accept();
                createClientHandler(socket);
            } catch (IOException e) {
                LOGGER.error("Error accepting a socket.", e);
            }
        }
    }

    protected abstract ISocketHandler<T> getSocketHandler(Socket socket);

    private void createClientHandler(Socket socket) {
        ISocketHandler<T> handler = getSocketHandler(socket);
        addClient(handler);
        Executors.newSingleThreadExecutor().submit(handler);
    }
}
