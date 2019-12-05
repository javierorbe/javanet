package com.javierorbe.net;

import java.net.Socket;

public abstract class ObjectSocketServer<T> extends SocketServer<T> {

    protected ObjectSocketServer(int port) {
        super(port);
    }

    @Override
    protected ISocketHandler<T> getSocketHandler(Socket socket) {
        return new ObjectSocketHandler<>() {
            @Override
            protected Socket getSocket() {
                return socket;
            }

            @Override
            public void receive(T t) {
                ObjectSocketServer.this.receive(this, t);
            }

            @Override
            public void run() {
                listen();
            }
        };
    }
}
