package com.boot.rest.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.rest.base.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
}
