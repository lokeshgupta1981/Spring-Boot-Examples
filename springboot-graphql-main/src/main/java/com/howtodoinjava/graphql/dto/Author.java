package com.howtodoinjava.graphql.dto;

public class Author {

	String name;

	public Author(String name) {
		super();
		this.name = name;
	}

	public Author() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
