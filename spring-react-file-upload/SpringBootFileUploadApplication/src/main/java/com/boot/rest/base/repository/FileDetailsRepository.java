package com.boot.rest.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.rest.base.model.FileDetails;

public interface FileDetailsRepository extends JpaRepository<FileDetails, Integer> {

}
