package com.ahmed.AhmedMohmoud.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import static java.io.File.separator;
import static java.lang.System.*;

@Slf4j
@Service
public class FileStorageService {

    private static final String STORAGE_DIRECTORY="D:\\uploads";

    public String saveFile(MultipartFile file, Integer id) throws IOException {
        if (file == null) {
            throw new RuntimeException("File to save is null");
        }
        File targetFile=new File(STORAGE_DIRECTORY+separator+file.getOriginalFilename());
        if (!Objects.equals(targetFile.getParent(), STORAGE_DIRECTORY)) {
            throw new SecurityException("Unsupported file name");
        }
        Files.copy(file.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return targetFile.getPath();
    }

}






















































//@Value("${file.upload.photos-output-path}")
//private String uploadFile;
//
//private static final String BASE_URL = "http://localhost:8080";
//
//private String getFileExtension(String fileName) {
//    if (fileName.isEmpty() || fileName == null) {
//        return "";
//    }
//    int dotIndex = fileName.lastIndexOf(".");
//    if (dotIndex == -1) {
//        return "";
//    }
//    return fileName.substring(dotIndex + 1).toLowerCase();
//}
//
//
//private String uploadFile(MultipartFile file, String fileUploadSubFolder) {
//    // .uploads/users/1
//    final String finalUploadPath = uploadFile + separator + fileUploadSubFolder;
//    File targetFile = new File(finalUploadPath);
//    if (!targetFile.exists()) {
//        boolean createdFile = targetFile.mkdirs();
//        if (!createdFile) {
//            log.warn("failed to create something");
//            return null;
//        }
//
//    }
//    final String fileExtension = getFileExtension(file.getOriginalFilename());
//    // .uploads/users/1/63462347.png
//    final String fileName = System.currentTimeMillis() + "." + fileExtension;
//    final String fileUploadFullPath = finalUploadPath + separator + fileName;
//    Path target = Paths.get(fileUploadFullPath);
//    try {
//        Files.write(target, file.getBytes());
//        log.info("File Saved Successfully to the target location : " + fileUploadFullPath);
//        return BASE_URL + "/uploads/" + fileUploadSubFolder + separator + fileName;
//    } catch (IOException e) {
//        log.error("File was not saved", e);
//    }
//    return null;
//}
//
//
//public String saveFile(MultipartFile file, Integer id) {
//    final String fileUploadSubFolder = "users" + separator + id;
//    return uploadFile(file, fileUploadSubFolder);
//}