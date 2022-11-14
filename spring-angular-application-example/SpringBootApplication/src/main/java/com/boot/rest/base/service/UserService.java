package com.boot.rest.base.service;

import java.util.List;

import com.boot.rest.base.model.User;

public interface UserService {

	public User insert(User userVO);

	public List<User> findAll();

	public void delete(int id);

	public User findById(int id);

	public User updateUser(int id, User userVO);
}
