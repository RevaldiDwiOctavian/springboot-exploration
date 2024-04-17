package com.example.api.demoapi.service;

import com.example.api.demoapi.dto.request.ProductRequest;
import com.example.api.demoapi.dto.response.ResponseMessage;
import com.example.api.demoapi.model.Product;
import com.example.api.demoapi.repository.ProductRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private JasperReportService reportService;

    public ResponseEntity<ResponseMessage> createProduct(ProductRequest request) {
        try {
            Set<ConstraintViolation<ProductRequest>> constraintViolationSet = validator.validate(request);

            if (!constraintViolationSet.isEmpty()) {
                String message = constraintViolationSet.iterator().next().getMessage();
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseMessage(message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null));
            }

            Product product = Product.builder()
                    .productName(request.getProductName())
                    .qty(request.getQty())
                    .build();
            productRepository.save(product);

            String message = "Successfully created product";

            return ResponseEntity
                    .badRequest()
                    .body(new ResponseMessage(message, HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(), product));
        }catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity
                    .internalServerError()
                    .body(new ResponseMessage(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null));
        }
    }

    public ResponseEntity<ResponseMessage> updateProduct(Long id, ProductRequest request) {
        try {
            Optional<Product> productOptional = productRepository.findById(id);
            if (productOptional.isEmpty()) {
                String message = "Product not found";
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseMessage(message, HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null));
            }

            Set<ConstraintViolation<ProductRequest>> violation = validator.validate(request);
            if (!violation.isEmpty()) {
                String message = violation.iterator().next().getMessage();
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseMessage(message, HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null));
            }

            Product product = productOptional.get();
            product.setProductName(request.getProductName());
            product.setQty(request.getQty());
            productRepository.save(product);

            String message = "Successfully updated product";
            return ResponseEntity
                    .ok()
                    .body(new ResponseMessage(message, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), product));
        }catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity
                    .internalServerError()
                    .body(new ResponseMessage(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null));
        }
    }

    public ResponseEntity<ResponseMessage> deleteProduct(Long id) {
        try {
            Optional<Product> productOptional = productRepository.findById(id);
            if (productOptional.isEmpty()) {
                String message = "Product not found";
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseMessage(message, HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null));
            }

            Product product = productOptional.get();
            productRepository.delete(product);

            String message = "Successfully deleted product";
            return ResponseEntity
                    .ok()
                    .body(new ResponseMessage(message, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), product));
        }catch (Exception e) {
            String message = e.getMessage();

            return ResponseEntity
                    .internalServerError()
                    .body(new ResponseMessage(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null));
        }
    }

    public ResponseEntity<ResponseMessage> getProduct(Long id) {
        try {
            Optional<Product> productOptional = productRepository.findById(id);
            if (productOptional.isEmpty()) {
                String message = "Product not found";
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseMessage(message, HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null));
            }

            Product product = productOptional.get();

            String message = "Successfully finding product";
            return ResponseEntity
                    .ok()
                    .body(new ResponseMessage(message, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), product));
        }catch (Exception e) {
            String message = e.getMessage();

            return ResponseEntity
                    .internalServerError()
                    .body(new ResponseMessage(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null));
        }
    }

    public ResponseEntity<ResponseMessage> getProducts(Pageable pageable) {
        try {
            Page<Product> product = productRepository.findAll(pageable);

            String message = "Successfully fetching product";
            return ResponseEntity
                    .ok()
                    .body(new ResponseMessage(message, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), product));
        }catch (Exception e) {
            String message = e.getMessage();

            return ResponseEntity
                    .internalServerError()
                    .body(new ResponseMessage(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null));
        }
    }

    public ResponseEntity<Resource> exportProducts(String format) throws JRException {
        List<Product> products = productRepository.findAll();

        byte[] reportContent = reportService.getProductReport(products, format);

        ByteArrayResource resource = new ByteArrayResource(reportContent);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("item-report." + format)
                                .build().toString())
                .body(resource);
    }

}
