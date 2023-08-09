package com.howtodoinjava.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final Path uploadDirectory = Paths.get("./images");

    @Override
    @PostConstruct
    public void init() {

        try {

            if (!Files.exists(uploadDirectory)) {

                Files.createDirectory(uploadDirectory);

            }

        } catch (Exception exception) {

            log.debug("some internal error occurred while creating directory: {} \n {}", exception.getMessage(), exception.getStackTrace());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "some internal error occurred while initialization");

        }

    }

    @Override
    public void save(MultipartFile file) {

        try {

            Files.copy(file.getInputStream(), uploadDirectory.resolve(Objects.requireNonNull(file.getOriginalFilename())));

        } catch (Exception exception) {

            log.debug("some internal error occurred while saving file: {} \n {}", exception.getMessage(), exception.getStackTrace());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "unable save file");

        }

    }

    @Override
    public List<String> getAll(){

        List<String> urls;

        try (Stream<Path> files = Files.walk(uploadDirectory, 1)) {

            urls = new ArrayList<>(files.filter(path -> !path.equals(uploadDirectory)).map(path -> "/image/get/" + uploadDirectory.relativize(path)).toList());

        }catch (Exception exception){

            log.debug("some internal error occurred while reading file: {} \n {}", exception.getMessage(), exception.getStackTrace());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "some internal error occurred");

        }

        return urls;
    }

    @Override
    public Resource load(String file) {

        try {

            Path path = uploadDirectory.resolve(file);

            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {

                return resource;

            } else {

                log.debug("some internal error occurred while reading file: {} \n {}", resource.getFilename(), resource.isReadable());

                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "unable to read the file");

            }
        } catch (Exception exception) {

            log.debug("some internal error occurred while reading file: {} \n {}", exception.getMessage(), exception.getStackTrace());

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "some internal error occurred");

        }

    }

}
