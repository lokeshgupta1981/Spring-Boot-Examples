package com.howtodoinjava.demo.entities;

import org.springframework.web.multipart.MultipartFile;

public class FileForm {

  private MultipartFile file;

  public FileForm() {
  }

  public FileForm(MultipartFile file) {
    this.file = file;
  }

  public MultipartFile getFile() {
    return file;
  }

  public void setFile(MultipartFile file) {
    this.file = file;
  }
}
