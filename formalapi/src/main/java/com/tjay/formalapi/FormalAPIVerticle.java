package com.tjay.formalapi;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.tjay.formalapi.entity.Product;
import com.tjay.formalapi.resources.ProductResources;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Hello world!
 *
 */
public class FormalAPIVerticle extends AbstractVerticle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FormalAPIVerticle.class);
	
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
        		
            	vertx.deployVerticle(new FormalAPIVerticle(), options);

       		}
       	
       	
        });
        
    	
    }
    
	@Override
	public void start() {
		LOGGER.info("Verticle FormalAPIVerticle Started");
		
//		vertx.createHttpServer()
//		.requestHandler(routingContext -> routingContext.response().end("<h1>Welcome to Vert.x Intro</h1>"))
//		.listen(8080);
		
		Router router = Router.router(vertx);
		
		router.route().handler(CookieHandler.create());
		
		// Create ProductResource object
		ProductResources productResources = new ProductResources();
		
		// Map subrouter for Products
		router.mountSubRouter("/api/", productResources.getAPISubRouter(vertx));
		
//		// Create Sub Router
//		Router apiSubRouter = Router.router(vertx);
//		
//		// API routing
//		apiSubRouter.route("/*").handler(productResources::defaultProcessorForAllAPI);
//		
//		apiSubRouter.route("/v1/products*").handler(BodyHandler.create());
//		apiSubRouter.get("/v1/products").handler(this::getAllProducts);
//		apiSubRouter.get("/v1/products/:id").handler(this::getProductById);
//		apiSubRouter.post("/v1/products").handler(this::addProduct);
//		apiSubRouter.put("/v1/products/:id").handler(this::updateProductById);
//		apiSubRouter.delete("/v1/products/:id").handler(this::deleteProductById);
//		
//		router.mountSubRouter("/api/", apiSubRouter);
		
		
		router.get("/yo.html").handler(routingContext -> {
			
			Cookie nameCookie = routingContext.getCookie("name");
			
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
				
				
				String name = "Unknown";
						
				if (nameCookie != null) {
					name = nameCookie.getValue();
				}
				else {
					nameCookie = Cookie.cookie("name", "Tom-Jay");
					nameCookie.setPath("/");
					nameCookie.setMaxAge(365 * 24 * 60 * 60); // 1 year in seconds
					
					routingContext.addCookie(nameCookie);
				}
				
				mappedHTML = replaceAllTokens(mappedHTML, "{name}", name);

			} catch (IOException e) {
				e.printStackTrace();
			}

			routingContext.response().putHeader("content-type", "text/html").end(mappedHTML);
			
		});
		
		// Default if no routes are matched
		router.route().handler(StaticHandler.create().setCachingEnabled(false));

		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port"), asyncResult -> {
			
			if (asyncResult.succeeded()) { 
				LOGGER.info("HTTP server running on port " + config().getInteger("http.port"));
			}
			else {
				LOGGER.error("Could not start a HTTP server", asyncResult.cause());
			}
			
			
		});

		

	}
	
	
//	// Called for all default API HTTP GET, POST, PUT and DELETE
//	private void defaultProcessorForAllAPI(RoutingContext routingContext) {
//		
//		String authToken = routingContext.request().getHeader("AuthToken");
//				
//		if (authToken == null || !authToken.equals("123")) {
//			LOGGER.info("Failed basic auth check");
//
//			routingContext.response().setStatusCode(401).putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(Json.encodePrettily(new JsonObject().put("error", "Not Authorized to use these API's")));
//		}
//		else {
//			LOGGER.info("Passed basic auth check");
//			
//			// Allowing CORS - Cross Domain API calls
//			routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
//			routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE");
//			
//			// Call next matching route
//			routingContext.next();			
//		}
//		
//	}
//	
//	// Get all products as array of products
//	private void getAllProducts(RoutingContext routingContext) {
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
//
//	}
//	
//	// Get one products that matches the input id and return as single json object
//	private void getProductById(RoutingContext routingContext) {
//		
//		final String productId = routingContext.request().getParam("id");
//		
//		String number = "123";
//		
//		Product firstItem = new Product(productId, number, "My item " + number);
//		
//		routingContext.response()
//		.setStatusCode(200)
//		.putHeader("content-type", "application/json")
//		.end(Json.encodePrettily(firstItem));
//
//
//		
//	}
//	
//	// Insert one item passed in from the http post body return what was added with unique id from the insert
//	private void addProduct(RoutingContext routingContext) {
//		
//		JsonObject jsonBody = routingContext.getBodyAsJson();
//		
//		System.out.println(jsonBody);
//		
//		String number = jsonBody.getString("number");
//		String description = jsonBody.getString("description");
//		
//		Product newItem = new Product("", number, description);
//		
//		// Add into database and get unique id
//		newItem.setId("556677");
//		
//		routingContext.response()
//		.setStatusCode(201)
//		.putHeader("content-type", "application/json")
//		.end(Json.encodePrettily(newItem));
//
//		
//	}
//	
//	// Update the item based on the url product id and return update product info
//	private void updateProductById(RoutingContext routingContext) {
//		
//		final String productId = routingContext.request().getParam("id");
//		
//		JsonObject jsonBody = routingContext.getBodyAsJson();
//
//
//		String number = jsonBody.getString("number");
//		String description = jsonBody.getString("description");
//		
//		Product updatedItem = new Product(productId, number, description);
//
//		routingContext.response()
//		.setStatusCode(200)
//		.putHeader("content-type", "application/json")
//		.end(Json.encodePrettily(updatedItem));
//
//		
//	}
//	
//	// Delete item and return 200 on success or 400 on fail
//	private void deleteProductById(RoutingContext routingContext) {
//		
//		final String productId = routingContext.request().getParam("id");
//
//		routingContext.response()
//		.setStatusCode(200)
//		.putHeader("content-type", "application/json")
//		.end();
//
//		
//	}
	
	
	public String replaceAllTokens(String input, String token, String newValue) {

		String output = input;

		while (output.indexOf(token) != -1) {

			output = output.replace(token, newValue);

		}

		return output;

	}

    
	@Override
	public void stop() {
		LOGGER.info("Verticle FormalAPIVerticle Stopped");
	}

	
}
