package com.example.api.demoapi.service;

import com.example.api.demoapi.dto.request.LoginRequest;
import com.example.api.demoapi.dto.request.RegisterRequest;
import com.example.api.demoapi.dto.response.AuthResponse;
import com.example.api.demoapi.dto.response.ResponseMessage;
import com.example.api.demoapi.model.User;
import com.example.api.demoapi.model.UserDetail;
import com.example.api.demoapi.repository.UserRepository;
import com.example.api.demoapi.util.JwtUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<ResponseMessage> register(RegisterRequest request) {
        try {
            Set<ConstraintViolation<RegisterRequest>> constraintViolationSet = validator.validate(request);

            if (!constraintViolationSet.isEmpty()) {
                ConstraintViolation<RegisterRequest> constraintViolation = constraintViolationSet.iterator().next();
                String message = constraintViolation.getMessage();
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseMessage(message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null));
            }

            if (userRepository.findByUsername(request.getUsername()) != null) {
                String message = "Username already exist";
                return ResponseEntity
                        .badRequest()
                        .body(new ResponseMessage(message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null));

            }

            User user = User.builder()
                    .username(request.getUsername())
                    .password(request.getPassword())
                    .build();
            user.hashPassword();

            userRepository.save(user);

            String message = "Register Success";

            return ResponseEntity
                    .ok()
                    .body(new ResponseMessage(message, HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), null));

        }catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity.internalServerError().body(new ResponseMessage(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null));
        }
    }

    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        try {
            Set<ConstraintViolation<LoginRequest>> constraintViolationSet = validator.validate(request);

            if (!constraintViolationSet.isEmpty()) {
                ConstraintViolation<LoginRequest> constraintViolation = constraintViolationSet.iterator().next();
                String message = constraintViolation.getMessage();
                return ResponseEntity
                        .badRequest()
                        .body(new AuthResponse(message, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateToken(authentication);

            UserDetail userDetail = (UserDetail) authentication.getPrincipal();

            String message = "Login success";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Token", jwt);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(AuthResponse.builder()
                            .data(new AuthResponse.UserData(userDetail.getUserId(), jwt, "Bearer", userDetail.getUsername()))
                            .message(message)
                            .statusCode(HttpStatus.OK.value())
                            .status(HttpStatus.OK.getReasonPhrase())
                            .build());
        } catch (AuthenticationException e){
            String message = "login error";
            return ResponseEntity
                    .badRequest()
                    .body(new AuthResponse(message, HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), null));
        }catch (Exception e) {
            String message = e.getMessage();
            return ResponseEntity
                    .badRequest()
                    .body(new AuthResponse(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null));
        }
    }
}
