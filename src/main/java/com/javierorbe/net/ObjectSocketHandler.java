package com.javierorbe.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ObjectSocketHandler<T> extends SocketHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectSocketHandler.class);

    private ObjectOutputStream outputStream;

    protected void listen() {
        setRunning(true);

        ObjectInputStream inputStream;
        try {
            outputStream = new ObjectOutputStream(getSocket().getOutputStream());
            inputStream = new ObjectInputStream(getSocket().getInputStream());
        } catch (IOException e) {
            LOGGER.error("Error getting I/O streams.", e);
            return;
        }

        try {
            while (getSocket().isConnected()) {
                try {
                    Object object = inputStream.readObject();
                    // TODO: test object class type
                    @SuppressWarnings("unchecked")
                    T paramObject = (T) object;
                    receive(paramObject);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            if (isRunning()) {
                setRunning(false);
            }
        }
    }

    @Override
    public void send(T t) {
        try {
            outputStream.writeObject(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
