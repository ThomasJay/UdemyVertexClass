package com.tomj.basicweb;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Hello world!
 *
 */
public class BasicWebVerticle extends AbstractVerticle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicWebVerticle.class);
	
    public static void main( String[] args ) {
    	
    	Vertx vertx = Vertx.vertx();
    	
    	vertx.deployVerticle(new BasicWebVerticle());
    	
    }
    
	@Override
	public void start() {
		LOGGER.info("Verticle BasicWebVerticle Started");
		
//		vertx.createHttpServer()
//		.requestHandler(routingContext -> routingContext.response().end("<h1>Welcome to Vert.x Intro</h1>"))
//		.listen(8080);
		
		Router router = Router.router(vertx);
		
		router.get("/yo.html").handler(routingContext -> {
			
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("webroot/yo.html").getFile());

			String mappedHTML = "";

			try {
				StringBuilder result = new StringBuilder("");

				Scanner scanner = new Scanner(file);

				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					result.append(line).append("\n");
				}

				scanner.close();

				mappedHTML = result.toString();

				mappedHTML = replaceAllTokens(mappedHTML, "{name}", "Tom Jay");

			} catch (IOException e) {
				e.printStackTrace();
			}

			routingContext.response().putHeader("content-type", "text/html").end(mappedHTML);
			
		});
		
		// Default if no routes are matched
		router.route().handler(StaticHandler.create().setCachingEnabled(false));

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);

		

	}
	
	public String replaceAllTokens(String input, String token, String newValue) {

		String output = input;

		while (output.indexOf(token) != -1) {

			output = output.replace(token, newValue);

		}

		return output;

	}

    
	@Override
	public void stop() {
		LOGGER.info("Verticle BasicWebVerticle Stopped");
	}

	
}
