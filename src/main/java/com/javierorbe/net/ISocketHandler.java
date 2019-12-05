package com.javierorbe.net;

public interface ISocketHandler<T> extends Runnable, AutoCloseable {

    void send(T t);

    void receive(T t);

    @Override
    void close();
}
