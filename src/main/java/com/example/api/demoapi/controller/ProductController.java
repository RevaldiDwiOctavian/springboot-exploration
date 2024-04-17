package com.example.api.demoapi.controller;

import com.example.api.demoapi.dto.request.ProductRequest;
import com.example.api.demoapi.dto.response.ResponseMessage;
import com.example.api.demoapi.model.Product;
import com.example.api.demoapi.service.ProductService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.embedded.TomcatVirtualThreadsWebServerFactoryCustomizer;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RestControllerAdvice
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    private TomcatVirtualThreadsWebServerFactoryCustomizer tomcatVirtualThreadsProtocolHandlerCustomizer;

    @PostMapping
    public ResponseEntity<ResponseMessage> createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getProducts(@PageableDefault Pageable pageable) {
        return productService.getProducts(pageable);
    }

    @GetMapping("/export/{format}")
    public ResponseEntity<Resource> exportProducts(@PathVariable String format) throws JRException {
        return productService.exportProducts(format);
    }
}
