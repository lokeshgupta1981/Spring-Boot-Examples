package com.howtodoinjava.library.web;

import org.jspecify.annotations.Nullable;

public record Book(String title, int copies, @Nullable String shelf) {
}
