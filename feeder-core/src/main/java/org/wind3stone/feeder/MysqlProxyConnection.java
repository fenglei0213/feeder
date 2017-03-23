package org.wind3stone.feeder;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetSocket;

/**
 * Created by fenglei on 2017/3/23.
 */
public class MysqlProxyConnection {

    private static final Logger logger = LoggerFactory.getLogger(MysqlProxyConnection.class);

    private final NetSocket clientSocket;
    private final NetSocket serverSocket;

    public MysqlProxyConnection(NetSocket clientSocket, NetSocket serverSocket) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
    }

    public void proxy() {
        // 当代理与mysql服务器连接关闭时，关闭client与代理的连接
        serverSocket.closeHandler(v -> clientSocket.close());
        // 反之亦然
        clientSocket.closeHandler(v -> serverSocket.close());
        // 不管那端的连接出现异常时，关闭两端的连接
        serverSocket.exceptionHandler(e -> {
            logger.error(e.getMessage(), e);
            close();
        });
        clientSocket.exceptionHandler(e -> {
            logger.error(e.getMessage(), e);
            close();
        });
        // 当收到来自客户端的数据包时，转发给mysql目标服务器
        clientSocket.handler(buffer -> serverSocket.write(buffer));
        // 当收到来自mysql目标服务器的数据包时，转发给客户端
        serverSocket.handler(buffer -> clientSocket.write(buffer));
    }

    private void close() {
        clientSocket.close();
        serverSocket.close();
    }
}
