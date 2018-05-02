package com.tjay.formalapi.resources;

import java.util.ArrayList;
import java.util.List;

import com.tjay.formalapi.entity.Product;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ProductResources {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductResources.class);

	
	public Router getAPISubRouter(Vertx vertx) {
		
		Router apiSubRouter = Router.router(vertx);
		
		// API routing
		apiSubRouter.route("/*").handler(this::defaultProcessorForAllAPI);
		
		apiSubRouter.route("/v1/products*").handler(BodyHandler.create());
		apiSubRouter.get("/v1/products").handler(this::getAllProducts);
		apiSubRouter.get("/v1/products/:id").handler(this::getProductById);
		apiSubRouter.post("/v1/products").handler(this::addProduct);
		apiSubRouter.put("/v1/products/:id").handler(this::updateProductById);
		apiSubRouter.delete("/v1/products/:id").handler(this::deleteProductById);
		
		return apiSubRouter;
	}
	
	
	// Called for all default API HTTP GET, POST, PUT and DELETE
	public void defaultProcessorForAllAPI(RoutingContext routingContext) {
		
		String authToken = routingContext.request().getHeader("AuthToken");
				
		if (authToken == null || !authToken.equals("123")) {
			LOGGER.info("Failed basic auth check");

			routingContext.response().setStatusCode(401).putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(Json.encodePrettily(new JsonObject().put("error", "Not Authorized to use these API's")));
		}
		else {
			LOGGER.info("Passed basic auth check");
			
			// Allowing CORS - Cross Domain API calls
			routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
			routingContext.response().putHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE");
			
			// Call next matching route
			routingContext.next();			
		}
		
	}
	
	// Get all products as array of products
	public void getAllProducts(RoutingContext routingContext) {
		
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
		
		Product firstItem = new Product("112233", "123", "My item 123");
		Product secondItem = new Product("11334455", "321", "My item 321");
		
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
	
	// Get one products that matches the input id and return as single json object
	public void getProductById(RoutingContext routingContext) {
		
		final String productId = routingContext.request().getParam("id");
		
		String number = "123";
		
		Product firstItem = new Product(productId, number, "My item " + number);
		
		routingContext.response()
		.setStatusCode(200)
		.putHeader("content-type", "application/json")
		.end(Json.encodePrettily(firstItem));


		
	}
	
	// Insert one item passed in from the http post body return what was added with unique id from the insert
	public void addProduct(RoutingContext routingContext) {
		
		JsonObject jsonBody = routingContext.getBodyAsJson();
		
		System.out.println(jsonBody);
		
		String number = jsonBody.getString("number");
		String description = jsonBody.getString("description");
		
		Product newItem = new Product("", number, description);
		
		// Add into database and get unique id
		newItem.setId("556677");
		
		routingContext.response()
		.setStatusCode(201)
		.putHeader("content-type", "application/json")
		.end(Json.encodePrettily(newItem));

		
	}
	
	// Update the item based on the url product id and return update product info
	public void updateProductById(RoutingContext routingContext) {
		
		final String productId = routingContext.request().getParam("id");
		
		JsonObject jsonBody = routingContext.getBodyAsJson();


		String number = jsonBody.getString("number");
		String description = jsonBody.getString("description");
		
		Product updatedItem = new Product(productId, number, description);

		routingContext.response()
		.setStatusCode(200)
		.putHeader("content-type", "application/json")
		.end(Json.encodePrettily(updatedItem));

		
	}
	
	// Delete item and return 200 on success or 400 on fail
	public void deleteProductById(RoutingContext routingContext) {
		
		final String productId = routingContext.request().getParam("id");

		routingContext.response()
		.setStatusCode(200)
		.putHeader("content-type", "application/json")
		.end();

		
	}

}
