package com.howtodoinjava.service;

import com.howtodoinjava.model.Profile;
import com.howtodoinjava.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public void save(Profile profile, MultipartFile file) {

        try {

            profile.setProfileImage(file.getBytes());

            profileRepository.save(profile);

        } catch (Exception e) {

            log.debug("Some internal error occurred", e);

        }
    }

    @Override
    public List<Profile> getAll() {

        return profileRepository.findAll();

    }
}
