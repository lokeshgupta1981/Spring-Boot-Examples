package com.springapi.uploading.service;
import java.util.List;
import com.springapi.uploading.model.Product;
import org.springframework.web.multipart.MultipartFile;


public interface ProductService {

    Product saveAttachment(MultipartFile file) throws Exception;
    void saveFiles(MultipartFile[] files) throws Exception;
    List<Product> getAllFiles();
    List<Product> getAllFiles2();

    void saveAllFilesList(List<Product> fileList);
}