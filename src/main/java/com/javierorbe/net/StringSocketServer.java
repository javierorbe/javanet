package com.javierorbe.net;

import java.net.Socket;

public abstract class StringSocketServer extends SocketServer<String> {

    protected StringSocketServer(int port) {
        super(port);
    }

    @Override
    protected ISocketHandler<String> getSocketHandler(Socket socket) {
        return new StringSocketHandler() {
            @Override
            protected Socket getSocket() {
                return socket;
            }

            @Override
            public void receive(String s) {
                StringSocketServer.this.receive(this, s);
            }

            @Override
            public void run() {
                listen();
            }
        };
    }
}
