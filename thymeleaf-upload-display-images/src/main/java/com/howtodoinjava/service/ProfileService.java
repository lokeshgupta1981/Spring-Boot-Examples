package com.howtodoinjava.service;

import com.howtodoinjava.model.Profile;
import com.howtodoinjava.repository.ProfileRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ProfileService {

  @Autowired
  private ProfileRepository profileRepository;

  public void save(Profile profile, MultipartFile file) {
    try {
      profile.setImageData(file.getBytes());
      profileRepository.save(profile);
    } catch (Exception e) {
      log.debug("Some internal error occurred", e);
    }
  }

  public List<Profile> getAll() {
    return profileRepository.findAll();
  }

  public Optional<Profile> findById(Long imageId) {
    return profileRepository.findById(imageId);
  }
}
