package com.javierorbe.net;

import java.util.concurrent.Future;

public interface ISocketServer<T> extends Runnable, AutoCloseable {

    Future<?> broadcast(T t);

    void receive(ISocketHandler<T> client, T t);
}
