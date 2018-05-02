package com.tomj.finishedapi;



import com.tomj.finishedapi.database.MongoManager;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

public class MongoDBVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBVerticle.class);
	
	public static MongoClient mongoClient = null;
	
	   public static void main( String[] args ) {
	    	
	    	VertxOptions vertxOptions = new VertxOptions();
	    	
		    vertxOptions.setClustered(true);
		    
		    Vertx.clusteredVertx(vertxOptions, results -> {
		    	
			      if (results.succeeded()) {
			    	  
			    	  Vertx vertx = results.result();
			    	  
				        // Use config/config.json from resources/classpath
				        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);

				        configRetriever.getConfig(config -> {
				        	
				           	if (config.succeeded()) {
				            		
				            		JsonObject configJson = config.result();
				            		
				            		// System.out.println(configJson.encodePrettily());
				             		
				            		DeploymentOptions options = new DeploymentOptions().setConfig(configJson);
				            		
				            		vertx.deployVerticle(new MongoDBVerticle(), options);
				           		}
				           	
				           	
				            });

			    	  
			    	  
			      }
			      
		    });


	    }
	 
		@Override
		public void start() {
			LOGGER.info("Verticle MongoDBVerticle Started");

			JsonObject dbConfig = new JsonObject();
			
			
			
			dbConfig.put("connection_string", "mongodb://" + config().getString("mongodb.host") + ":" + config().getInteger("mongodb.port") + "/" + config().getString("mongodb.databasename"));
//			dbConfig.put("username", config().getString("mongodb.username"));
//			dbConfig.put("password", config().getString("mongodb.password"));
//			dbConfig.put("authSource", config().getString("mongodb.authSource"));
			dbConfig.put("useObjectId", true);
			
			mongoClient = MongoClient.createShared(vertx, dbConfig);
			
			MongoManager mongoManager = new MongoManager(mongoClient);
			
			mongoManager.registerConsumer(vertx);
			

		}
	
		@Override
		public void stop() {
			LOGGER.info("Verticle VertxMongoVerticle Stopped");
		}

		
}
