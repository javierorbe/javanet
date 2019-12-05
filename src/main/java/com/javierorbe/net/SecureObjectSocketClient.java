package com.javierorbe.net;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SecureObjectSocketClient<T> extends SecureObjectSocketHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureObjectSocketClient.class);

    private final String hostname;
    private final int port;
    private final TrustManager[] trustManagers;

    private SSLSocket socket;

    protected SecureObjectSocketClient(String hostname, int port, TrustManager[] trustManagers) {
        this.hostname = hostname;
        this.port = port;
        this.trustManagers = trustManagers;
    }

    @Override
    public void run() {
        SSLSocketFactory socketFactory;
        try {
            socketFactory = createSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOGGER.error("Error creating socket factory.", e);
            return;
        }

        try {
            socket = (SSLSocket) socketFactory.createSocket(hostname, port);
            socket.addHandshakeCompletedListener(e -> LOGGER.trace("Handshake completed."));
        } catch (IOException e) {
            LOGGER.error("Could not create a socket.", e);
            return;
        }

        super.run();
    }

    @Override
    protected SSLSocket getSocket() {
        return socket;
    }

    private SSLSocketFactory createSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new SecureRandom());
        return sslContext.getSocketFactory();
    }
}
