package org.wind3stone.feeder;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by fenglei on 2017/3/23.
 */
public class Bootstrap {

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        logger.info("MySQL Proxy Server Starting ...");
        Vertx.vertx().deployVerticle(new MySQLProxyServerVerticle());
    }
}
