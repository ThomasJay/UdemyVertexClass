package com.tomj.vertxmongo;


import java.util.List;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Hello world!
 *
 */
public class VertxMongoVerticle extends AbstractVerticle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VertxMongoVerticle.class);
			
	public static MongoClient mongoClient = null;
	
    public static void main( String[] args ) {
    	
    	VertxOptions vertxOptions = new VertxOptions();
    	
	    vertxOptions.setClustered(true);
	    
	    Vertx.clusteredVertx(vertxOptions, results -> {
	    	
		      if (results.succeeded()) {
		    	  
		    	  Vertx vertx = results.result();
		    	  
		    	  vertx.deployVerticle(new VertxMongoVerticle());
		      }
		      
	    });
	    

//    	Vertx vertx = Vertx.vertx();
    	
//    	vertx.deployVerticle(new VertxMongoVerticle());
    }
    
	@Override
	public void start() {
		LOGGER.info("Verticle VertxMongoVerticle Started");
		
		Router router = Router.router(vertx);
		
		router.get("/mongofind").handler(this::getAllProducts);
		
		JsonObject dbConfig = new JsonObject();
		
		dbConfig.put("connection_string", "mongodb://localhost:27017/MongoTest");
//		dbConfig.put("username", "admin");
//		dbConfig.put("password", "password");
//		dbConfig.put("authSource", "MongoTest");
		dbConfig.put("useObjectId", true);
		
		mongoClient = MongoClient.createShared(vertx, dbConfig);
		

//		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
//		
//		vertx.eventBus().consumer("com.tomj.myservice", message -> {
//			
//			System.out.println("Recevied message: " + message.body());
//			
//			message.reply(new JsonObject().put("responseCode", "OK").put("message", "This is your response to your event"));
//			
//		});
		
	    vertx.setTimer(5000, handler ->{
	    	
	    	sendTestEvent();
	    	
	    });

		
	}
	
	private void sendTestEvent() {
		
	    JsonObject testInfo = new JsonObject();
	    
	    testInfo.put("info", "Hi");

	    System.out.println("Sending message=" + testInfo.toString());
	    		
		vertx.eventBus().send("com.tomj.myservice", testInfo.toString(), reply -> {
			
			if (reply.succeeded()) {
				JsonObject replyResults = (JsonObject) reply.result().body();
				
				System.out.println("Got Reply message=" + replyResults.toString());
			}
			
		});
	}
	
	// Get all products as array of products
	private void getAllProducts(RoutingContext routingContext) {
		
		
		FindOptions findOptions = new FindOptions();
		
		findOptions.setLimit(1);
		
		mongoClient.findWithOptions("products", new JsonObject(), findOptions, results -> {
			
			try {
				List<JsonObject> objects = results.result();
				
				if (objects != null && objects.size() != 0) {
					
					System.out.println("Got some data len=" + objects.size());
					
					JsonObject jsonResponse = new JsonObject();
					
					jsonResponse.put("products", objects);
					
	    	  		routingContext.response()
    	  			.putHeader("content-type", "application/json; charset=utf-8")
    	  			.setStatusCode(200)
    	  			.end(Json.encodePrettily(jsonResponse));

					
				}
				else {

	    	  		JsonObject jsonResponse = new JsonObject();
	    	  	      
	    	  		jsonResponse.put("error", "No items found");
	      
	    	  		routingContext.response()
	    	  			.putHeader("content-type", "application/json; charset=utf-8")
	    	  			.setStatusCode(400)
	    	  			.end(Json.encodePrettily(jsonResponse));

				}
				
			}
			catch(Exception e) {
				LOGGER.info("getAllProducts Failed exception e=", e.getLocalizedMessage());
				
    	  		JsonObject jsonResponse = new JsonObject();
  	  	      
    	  		jsonResponse.put("error", "Exception and No items found");
      
    	  		routingContext.response()
    	  			.putHeader("content-type", "application/json; charset=utf-8")
    	  			.setStatusCode(400)
    	  			.end(Json.encodePrettily(jsonResponse));

			}
			
		});
		
//		
//		JsonObject responseJson = new JsonObject();
//		
////		JsonArray items = new JsonArray();
//
//		
////		JsonObject firstItem = new JsonObject();
////		firstItem.put("number", "123");
////		firstItem.put("description", "My item 123");
////		
////		items.add(firstItem);
////		
////		JsonObject secondItem = new JsonObject();
////		secondItem.put("number", "321");
////		secondItem.put("description", "My item 321");
////		
////		items.add(secondItem);
////		
////		responseJson.put("products", items);
//		
//		Product firstItem = new Product("112233", "123", "My item 123");
//		Product secondItem = new Product("11334455", "321", "My item 321");
//		
//		List<Product> products = new ArrayList<Product>();
//		
//		products.add(firstItem);
//		products.add(secondItem);
//		
//		responseJson.put("products", products);
//		
//		routingContext.response()
//			.setStatusCode(200)
//			.putHeader("content-type", "application/json")
//			.end(Json.encodePrettily(responseJson));
//
////		routingContext.response()
////		.setStatusCode(400)
////		.putHeader("content-type", "application/json")
////		.end(Json.encodePrettily(new JsonObject().put("error", "Could not find all products")));

	}

	
    
	@Override
	public void stop() {
		LOGGER.info("Verticle VertxMongoVerticle Stopped");
	}

}
