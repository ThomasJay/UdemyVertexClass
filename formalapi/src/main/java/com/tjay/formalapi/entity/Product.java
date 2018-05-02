package com.tjay.formalapi.entity;

public class Product {

	private String id;
	private String number;
	private String description;

	public Product(String id, String number, String description) {
		this.id = id;
		this.number = number;
		this.description = description;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	
}
