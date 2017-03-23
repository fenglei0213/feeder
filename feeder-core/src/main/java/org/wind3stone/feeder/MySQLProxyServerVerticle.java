package org.wind3stone.feeder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by fenglei on 2017/3/23.
 */
public class MySQLProxyServerVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MySQLProxyServerVerticle.class);

    private int mysqlPort = 3306;
    private static String MYSQL_PROXY_IP = "";

    static {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            logger.error("InetAddress.getLocalHost UnknownHostException", e);
            System.exit(1);
        }
        MySQLProxyServerVerticle.MYSQL_PROXY_IP = address.getHostAddress();
    }

    @Override
    public void start() throws Exception {
        // 创建代理服务器
        NetServer netServer = vertx.createNetServer();
        // 创建连接mysql客户端
        NetClient netClient = vertx.createNetClient();
        netServer.connectHandler(socket -> netClient.connect(mysqlPort,
                MySQLProxyServerVerticle.MYSQL_PROXY_IP, result -> {
            // 响应来自客户端的连接请求，成功之后，在建立一个与目标mysql服务器的连接
            if (result.succeeded()) {
                // 与目标mysql服务器成功连接连接之后，创造一个MysqlProxyConnection对象,并执行代理方法
                new MysqlProxyConnection(socket, result.result()).proxy();
            } else {
                logger.error(result.cause().getMessage(), result.cause());
                socket.close();
            }
        })).listen(mysqlPort, listenResult -> {
            // 代理服务器的监听端口
            if (listenResult.succeeded()) {
                // 成功启动代理服务器
                logger.info("Mysql proxy server startup SUCCESS .");
                logger.info("ip:{},port:{}", MySQLProxyServerVerticle.MYSQL_PROXY_IP
                        , mysqlPort);
            } else {
                // 启动代理服务器失败
                logger.error("Mysql proxy exit. because: {}"
                        , listenResult.cause().getMessage(), listenResult.cause());
                System.exit(1);
            }
        });
    }

    /**
     * setMysqlPort setMysqlPort
     *
     * @param mysqlPort
     */
    public void setMysqlPort(int mysqlPort) {
        this.mysqlPort = mysqlPort;
    }


}
