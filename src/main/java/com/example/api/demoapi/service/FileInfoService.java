package com.example.api.demoapi.service;

import com.example.api.demoapi.dto.response.ResponseMessage;
import com.example.api.demoapi.model.FIleInfo;
import com.example.api.demoapi.repository.FileInfoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.util.ImageUtil;
import org.apache.coyote.Response;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class FileInfoService {
    @Autowired
    private FileInfoRepository fileInfoRepository;

    public ResponseEntity<ResponseMessage> uploadImage(MultipartFile[] multipartFile) throws IOException {
        for (MultipartFile file : multipartFile) {
            fileInfoRepository.save(FIleInfo.builder()
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .fileData(file.getBytes())
                    .build());
        }

        return ResponseEntity.ok().body(new ResponseMessage("File uploaded successfully", HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), null));
    }

    @Transactional
    public ResponseEntity<ResponseMessage> getFileInfo(String fileName) {
        Optional<FIleInfo> fileInfo = fileInfoRepository.findByFileName(fileName);
        return fileInfo.map(info -> ResponseEntity.ok().body(new ResponseMessage("Found it", HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), info))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<byte[]> getFile(String fileName) {
        FIleInfo fileInfo = fileInfoRepository.findByFileName(fileName).orElseThrow();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getFileName() + "\"")
                .contentType(MediaType.valueOf("image/png"))
                .body(fileInfo.getFileData());
    }
}
