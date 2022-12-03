package com.boot.rest.base.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.rest.base.model.User;
import com.boot.rest.base.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {
  @Autowired
  private UserRepository userRepository;

  @Override
  public User insert(User userVO) {
    return this.userRepository.save(userVO);
  }

  @Override
  public List<User> findAll() {

    return this.userRepository.findAll();
  }

  @Override
  public void delete(int id) {
    this.userRepository.deleteById(id);
  }

  @Override
  public User findById(int id) {
    return this.userRepository.findById(id).get();
  }

  @Override
  public User updateUser(int id, User userVO) {
    userVO.setId(id);
    return this.userRepository.save(userVO);
  }

}
