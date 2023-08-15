package com.howtodoinjava.service;

import com.howtodoinjava.model.Profile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {

    void save(Profile profile, MultipartFile file);

    List<Profile> getAll();

}
