package com.javierorbe.net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executors;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Socket server that uses TLS connection. */
public abstract class SecureSocketServer<T> extends AbstractSocketServer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecureSocketServer.class);

    private final int port;
    private final Path keyPath;
    private final String keyPassword;

    private SSLServerSocket serverSocket;

    protected SecureSocketServer(int port, Path keyPath, String keyPassword) {
        this.port = port;
        this.keyPath = keyPath;
        this.keyPassword = keyPassword;
    }

    @Override
    public SSLServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {
        try {
            SSLServerSocketFactory ssf = createServerSocketFactory(keyPath, keyPassword);
            serverSocket = (SSLServerSocket) ssf.createServerSocket(port);
        } catch (Exception e) {
            LOGGER.error("Could not create a server socket.", e);
            return;
        }

        LOGGER.info("Server started listening on port {}.", port);
        listen();
    }

    private void listen() {
        setRunning(true);

        while (isRunning()) {
            try {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                createClientHandler(socket);
            } catch (IOException e) {
                LOGGER.error("Error accepting a socket.", e);
            }
        }
    }

    protected abstract ISocketHandler<T> getSocketHandler(SSLSocket socket);

    private void createClientHandler(SSLSocket socket) {
        ISocketHandler<T> handler = getSocketHandler(socket);
        addClient(handler);
        Executors.newSingleThreadExecutor().submit(handler);
    }

    private static SSLServerSocketFactory createServerSocketFactory(Path keyPath, String keyPassword)
            throws KeyStoreException, IOException, NoSuchAlgorithmException,
            KeyManagementException, CertificateException, UnrecoverableKeyException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(Files.newInputStream(keyPath), keyPassword.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, keyPassword.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext sc = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = tmf.getTrustManagers();
        sc.init(kmf.getKeyManagers(), trustManagers, null);

        return sc.getServerSocketFactory();
    }
}
