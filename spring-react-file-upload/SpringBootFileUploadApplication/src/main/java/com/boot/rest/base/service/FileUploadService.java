package com.boot.rest.base.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.boot.rest.base.model.FileDetails;
import com.boot.rest.base.payload.FileUploadResponse;

public interface FileUploadService {

	public FileUploadResponse uploadFile(MultipartFile file, String uploaderName) throws IOException;

	public Resource loadFileAsResource(String fileName) throws FileNotFoundException;
	
	public List<FileDetails> getAllFiles();
}
