package com.tomj.firstvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App  extends AbstractVerticle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
    public static void main( String[] args )
    {
    	Vertx vertx = Vertx.vertx();
    	
    	vertx.deployVerticle(new App());
    }
    
    @Override
    public void start() {
    	LOGGER.info("Verticle App Started");
    }
    
    @Override
    public void stop() {
    	LOGGER.info("Verticle App Stopped");
    }
    
    
}
