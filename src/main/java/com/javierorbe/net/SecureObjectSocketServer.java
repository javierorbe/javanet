package com.javierorbe.net;

import java.nio.file.Path;
import javax.net.ssl.SSLSocket;

public abstract class SecureObjectSocketServer<T> extends SecureSocketServer<T> {

    protected SecureObjectSocketServer(int port, Path keyPath, String keyPassword) {
        super(port, keyPath, keyPassword);
    }

    @Override
    protected ISocketHandler<T> getSocketHandler(SSLSocket socket) {
        return new SecureObjectSocketHandler<>() {
            @Override
            protected SSLSocket getSocket() {
                return socket;
            }

            @Override
            public void receive(T t) {
                SecureObjectSocketServer.this.receive(this, t);
            }
        };
    }
}
