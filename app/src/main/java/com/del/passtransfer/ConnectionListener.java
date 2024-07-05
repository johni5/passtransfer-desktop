package com.del.passtransfer;

/**
 * Created by DodolinEL
 * date: 03.07.2024
 */
public interface ConnectionListener {

    void connected(String cid);

    void error(String msg);

    void notyfyApp(String name);

    void message(String msg);

    void disconnected();

}
