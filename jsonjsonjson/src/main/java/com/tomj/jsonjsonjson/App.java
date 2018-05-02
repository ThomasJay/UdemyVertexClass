package com.tomj.jsonjsonjson;

import io.vertx.core.json.JsonObject;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

    	String jsonString = "{\"name\":\"Tom\"}";
    	
    	JsonObject jsonObject = new JsonObject();
    	
    	jsonObject.put("name", "Fred").put("location", "San Francisco").put("address", "100 A Street");
    	
    	System.out.println("Json=" + jsonObject.toString());
    	System.out.println("name=" + jsonObject.getString("name"));
    	
    	MyItem myItem = new MyItem();
    	
    	myItem.setName("Tom");
    	myItem.setDescription("Programmer");
    	
    	JsonObject jsonObject2 = JsonObject.mapFrom(myItem);
    	
    	
    	System.out.println("myItem=" + jsonObject2.toString());
    	
    	MyItem myItem2 = jsonObject2.mapTo(MyItem.class);
    	
    	System.out.println("myItem2.name=" + myItem2.getName());

    	
    }
}
