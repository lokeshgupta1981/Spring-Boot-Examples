package com.howtodoinjava.graphql.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.howtodoinjava.graphql.dto.Book;
import com.howtodoinjava.graphql.repository.BookRepository;

@Controller
public class BookController {

	@Autowired
	private BookRepository bookRepository;

	@SchemaMapping(typeName = "Query", value="allBooks")
	public List<Book> getAllBooks() {
		return bookRepository.allBooks();
	}

	@QueryMapping
	public Book bookById(@Argument Integer id) {
		return bookRepository.bookById(id);
	}

	@MutationMapping
	public Book updateBook(@Argument Integer id, @Argument String title) {
		return bookRepository.updateBook(id, title);
	}

}
