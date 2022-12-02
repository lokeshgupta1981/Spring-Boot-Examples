package com.howtodoinjava.graphql.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.howtodoinjava.graphql.dto.Author;
import com.howtodoinjava.graphql.dto.Book;

@Component
public class BookRepository {

	private static List<Book> books;

	static {
		Book book1 = new Book(1, "GraphQL Overview", 100, new Author("Amit"));
		Book book2 = new Book(2, "Spring Overview", 100, new Author("Lokesh"));
		books = List.of(book1, book2);
	}

	public List<Book> allBooks() {
		return books;
	}

	public Book bookById(Integer id) {
		for (Book book : books) {
			if (book.getId() == id)
				return book;
		}
		return null;
	}

	public Book updateBook(Integer id, String title) {
		Book book = bookById(id);
		book.setTitle(title);
		/*
		 * if (book != null) { book.setTitle(title); }
		 */
		return book;
	}

}
