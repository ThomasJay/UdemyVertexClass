package com.tomj.helloworldapi;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.tomj.helloworldapi.entity.Product;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Hello world!
 *
 */
public class HelloWorldAPIVerticle extends AbstractVerticle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldAPIVerticle.class);
	
    public static void main( String[] args ) {
    	
//    	DeploymentOptions options = new DeploymentOptions();
//    	
//    	options.setConfig(new JsonObject().put("http.port", 8080));
    			
    	Vertx vertx = Vertx.vertx();
    	
        // Use config/config.json from resources/classpath
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);

        configRetriever.getConfig(config -> {
        	
       	if (config.succeeded()) {
        		
        		JsonObject configJson = config.result();
        		
        		System.out.println(configJson.encodePrettily());
         		
        		DeploymentOptions options = new DeploymentOptions().setConfig(configJson);
        		
            	vertx.deployVerticle(new HelloWorldAPIVerticle(), options);

       		}
       	
       	
        });
        
    	
    }
    
	@Override
	public void start() {
		LOGGER.info("Verticle HelloWorldAPIVerticle Started");
		
//		vertx.createHttpServer()
//		.requestHandler(routingContext -> routingContext.response().end("<h1>Welcome to Vert.x Intro</h1>"))
//		.listen(8080);
		
		Router router = Router.router(vertx);
		
		
		// API routing
		router.get("/api/v1/products").handler(this::getAllProducts);
		
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

		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port"));

		

	}
	
	
	private void getAllProducts(RoutingContext routingContext) {
		
		JsonObject responseJson = new JsonObject();
		
//		JsonArray items = new JsonArray();

		
//		JsonObject firstItem = new JsonObject();
//		firstItem.put("number", "123");
//		firstItem.put("description", "My item 123");
//		
//		items.add(firstItem);
//		
//		JsonObject secondItem = new JsonObject();
//		secondItem.put("number", "321");
//		secondItem.put("description", "My item 321");
//		
//		items.add(secondItem);
//		
//		responseJson.put("products", items);
		
		Product firstItem = new Product("123", "My item 123");
		Product secondItem = new Product("321", "My item 321");
		
		List<Product> products = new ArrayList<Product>();
		
		products.add(firstItem);
		products.add(secondItem);
		
		responseJson.put("products", products);
		
		routingContext.response()
			.setStatusCode(200)
			.putHeader("content-type", "application/json")
			.end(Json.encodePrettily(responseJson));

//		routingContext.response()
//		.setStatusCode(400)
//		.putHeader("content-type", "application/json")
//		.end(Json.encodePrettily(new JsonObject().put("error", "Could not find all products")));

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
		LOGGER.info("Verticle HelloWorldAPIVerticle Stopped");
	}

	
}
