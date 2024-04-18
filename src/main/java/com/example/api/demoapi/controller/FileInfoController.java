package com.example.api.demoapi.controller;

import com.example.api.demoapi.dto.response.ResponseMessage;
import com.example.api.demoapi.model.FIleInfo;
import com.example.api.demoapi.service.FileInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileInfoController {
    @Autowired
    private FileInfoService fileInfoService;

    @PostMapping
    public ResponseEntity<ResponseMessage> save(@RequestParam("file") MultipartFile[] file) throws IOException {
        return fileInfoService.uploadImage(file);
    }

    @GetMapping("/get")
    public ResponseEntity<byte[]> get(@RequestParam("filename") String filename) {
        return fileInfoService.getFile(filename);
    }
}
