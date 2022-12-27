package com.boot.rest.base.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.boot.rest.base.exception.FileNotSupportedException;
import com.boot.rest.base.model.FileDetails;
import com.boot.rest.base.payload.FileUploadResponse;
import com.boot.rest.base.service.FileUploadService;

@RestController
@RequestMapping(value = "file")
@CrossOrigin(origins = "http://localhost:3000")
public class FileUploadController {

	@Autowired
	private FileUploadService fileUploadService;

	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<FileDetails> getAllFiles() {
		return this.fileUploadService.getAllFiles();
	}

	private FileUploadResponse uploadSingleFile(String name, MultipartFile file) throws IOException {
		return fileUploadService.uploadFile(file, name);
	}

	@PostMapping(value = "/upload")
	public ResponseEntity<Object> uploadFiles(@RequestParam("name") String name,
			@RequestParam("files") MultipartFile[] files) {

		try {
			List<FileUploadResponse> fileUploadResponses = Arrays.stream(files).map(file -> {
				try {
					return uploadSingleFile(name, file);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}).collect(Collectors.toList());
			
			return new ResponseEntity<>(fileUploadResponses, HttpStatus.OK);
		} catch (UncheckedIOException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (FileNotSupportedException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity<Object> downloadFile(@PathVariable String fileName, HttpServletRequest request) {

		try {
			Resource resource = this.fileUploadService.loadFileAsResource(fileName);
			String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (IOException ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
}
