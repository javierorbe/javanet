package com.javierorbe.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StringSocketHandler extends SocketHandler<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringSocketHandler.class);

    private DataOutputStream outputStream;

    protected void listen() {
        DataInputStream inputStream;
        try {
            outputStream = new DataOutputStream(getSocket().getOutputStream());
            inputStream = new DataInputStream(getSocket().getInputStream());
        } catch (IOException e) {
            LOGGER.error("Error getting I/O streams.", e);
            return;
        }

        while (getSocket().isConnected()) {
            try {
                String str = inputStream.readUTF();
                receive(str);
            } catch (IOException e) {
                LOGGER.error("Error reading from the input stream.", e);
            }
        }
    }

    @Override
    public void send(String s) {
        try {
            outputStream.writeUTF(s);
        } catch (IOException e) {
            LOGGER.error("Error writing to the output stream.", e);
        }
    }
}
