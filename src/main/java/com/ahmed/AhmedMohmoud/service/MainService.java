package com.ahmed.AhmedMohmoud.service;

import com.ahmed.AhmedMohmoud.dao.MessageRepo;
import com.ahmed.AhmedMohmoud.dao.UserRepo;
import com.ahmed.AhmedMohmoud.entities.Message;
import com.ahmed.AhmedMohmoud.entities.User;
import com.ahmed.AhmedMohmoud.exception.FileTooLargeException;
import com.ahmed.AhmedMohmoud.exception.InvalidFileException;
import com.ahmed.AhmedMohmoud.exception.OperationNotPermittedException;
import com.ahmed.AhmedMohmoud.exception.ResourceNotFoundException;
import com.ahmed.AhmedMohmoud.helpers.*;
import io.netty.channel.ChannelOption;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.hc.client5.http.entity.mime.ByteArrayBody;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;


import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MainService {

    private static final Logger logger = LoggerFactory.getLogger(MainService.class);
    @Value(value = "${ImgurClientId}")
    private String ImgurClientId;
    private final UserRepo userRepo;
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.imgur.com/3")
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create()
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 10s connect timeout
                            .responseTimeout(Duration.ofSeconds(30)) // 30s response timeout
            ))
            .build();
    private final MessageRepo messageRepo;
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/gif" ,"image/jpg");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;


    public ResponseEntity<ProfileDto> getProfileDto(Authentication connectedUser) {
        User u = (User) connectedUser.getPrincipal();
        ProfileDto pd = ProfileDto.builder()
                .id(u.getId())
                .bio(u.getBio())
                .name(u.getFullName())
                .picUrl(u.getPicUrl())
                .build();
        return ResponseEntity.ok(pd);
    }


    public ResponseEntity<UserMessagesResponse> getMessages(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable p = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Message> sentMessages = messageRepo.getPaginatedSentMessages(p, user.getId());
        Page<Message> receivedMessages = messageRepo.getPaginatedReceivedMessages(p, user.getId());
        UserMessagesResponse userMessagesResponse = UserMessagesResponse
                .builder()
                .sentMessages(sentMessages)
                .receivedMessages(receivedMessages)
                .build();
        return ResponseEntity.ok(userMessagesResponse);
    }

    public ResponseEntity<List<SearchByNameHelper>> getUserByName(String name) {
        String name2 = "%" + name + "%";
        List<User> u = userRepo.findUserByName(name2);
        List<SearchByNameHelper> list = u.stream().
                map(user -> SearchByNameHelper.builder()
                        .id(user.getId())
                        .name(user.getFullName())
                        .picUrl(user.getPicUrl())
                        .bio(user.getBio())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);

    }


    public ResponseEntity<String> deleteMessage(Integer msgId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Message m = messageRepo.findById(msgId)
                .orElseThrow(() -> new ResourceNotFoundException("Message doesn't exist"));
        if (!m.getReceiver().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("Only the receiver can delete the message");

        }
        int deletedRows = messageRepo.deleteSpecificMessageReceivedToSpecificUser(msgId, user.getId());
        if (deletedRows == 0) {
            throw new OperationNotPermittedException("The message is not deleted");
        }
        return ResponseEntity.ok("Deleted Successfully");
    }

    public ResponseEntity<String> makeMessageFavorite(Integer msgId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Message message = messageRepo.makeMessageFavorite(msgId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Message doesn't exist"));
        if (!message.getReceiver().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("Only the receiver can delete the message");
        }
        message.setFavourite(!message.isFavourite());
        messageRepo.save(message);
        if (message.isFavourite()) {
            return ResponseEntity.ok("Message is favorite now");
        }
        return ResponseEntity.ok("Message is not favorite now");
    }

    public ResponseEntity<SettingsDto> getUserSettings(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        if (user != null) {
            SettingsDto settingsDto = SettingsDto.builder()
                    .id(user.getId())
                    .name(user.getFullName())
                    .bio(user.getBio())
                    .picUrl(user.getPicUrl())
                    .dateOfBirth(user.getDateOfBirth())
                    .build();
            return ResponseEntity.ok(settingsDto);
        }
        throw new ResourceNotFoundException("User not found");
    }

    public ResponseEntity<String> updateUserSettings(SettingsDto settingsDto, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        User existingUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (settingsDto.getName() != null) {
            existingUser.setName(settingsDto.getName());
        }
        if (settingsDto.getPicUrl() != null) {
            existingUser.setPicUrl(settingsDto.getPicUrl());
        }
        if (settingsDto.getBio() != null) {
            existingUser.setBio(settingsDto.getBio());
        }
        if (settingsDto.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(settingsDto.getDateOfBirth());
        }
        userRepo.save(existingUser);
        return ResponseEntity.ok("Settings updated successfully");
    }

    public ResponseEntity<List<TopThreeResponseHelper>> getTopThreeRankedReceivers() {
        List<TopThreeQueryHelper> list = userRepo.getTopThreeReceivers();
        int rank = 1;
        List<TopThreeResponseHelper> responseHelperList = new ArrayList<>();
        for (TopThreeQueryHelper l : list) {
            responseHelperList.add(TopThreeResponseHelper.builder()
                    .id(l.getId())
                    .messageCount(l.getMessageCount())
                    .picUrl(l.getPicUrl())
                    .bio(l.getBio())
                    .name(l.getName())
                    .rank(rank++)
                    .build());
        }
        return ResponseEntity.ok(responseHelperList);


    }


    public ResponseEntity<String> sendMessage(Integer receiverId, SendMessageRequest content) {
        User receiver = userRepo.findById(receiverId).orElseThrow(()  -> new ResourceNotFoundException("User not found"));
        Message m = Message.builder()
                .content(content.getMessageContent())
                .receiver(receiver)
                .isFavourite(false)
                .build();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            m.setSender(null);
            m.setCreatedBy(-1);
        } else {
            User u = userRepo.findByUserEmail(auth.getName());
            if (u == null) {
                throw new ResourceNotFoundException("Sender not found");
            }
            m.setSender(u);
        }
        messageRepo.save(m);
        return ResponseEntity.ok("Successfully saved");


    }

    public ResponseEntity<String> uploadProfilePicture(MultipartFile file, Authentication connectedUser) {
        Logger logger = LoggerFactory.getLogger(MainService.class);

        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new InvalidFileException("Invalid file type! Only JPG ,JPEG, PNG, GIF allowed.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileTooLargeException("File too large! Max: 10MB");
        }


            User user = (User) connectedUser.getPrincipal();
            int maxRetries = 3;
            int initialDelaySeconds = 5;

            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    CompletableFuture<String> future = webClient.post()
                            .uri("/image")
                            .header("Authorization", "Client-ID " + ImgurClientId)
                            .bodyValue(file.getBytes())
                            .retrieve()
                            .toEntity(String.class)
                            .map(responseEntity -> {
                                logger.info("Imgur Headers: {}", responseEntity.getHeaders());
                                return responseEntity.getBody();
                            })
                            .toFuture();

                    String response = future.get();
                    JSONObject jsonObject = new JSONObject(response);
                    String imageUrl = jsonObject.getJSONObject("data").getString("link");

                    user.setPicUrl(imageUrl);
                    userRepo.save(user);
                    logger.info("Profile picture uploaded successfully for user: {}, URL: {}", user.getEmail(), imageUrl);
                    return ResponseEntity.ok("Profile picture uploaded successfully: " + imageUrl);

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    String errorMsg = cause.getMessage();
                    if (errorMsg.contains("RST_STREAM")) {
                        logger.warn("RST_STREAM received on attempt {}/{}", attempt, maxRetries);
                        if (attempt < maxRetries) {
                            int delay = initialDelaySeconds * (int) Math.pow(2, attempt - 1); // Exponential backoff
                            logger.info("Retrying after {} seconds...", delay);
                            try {
                                Thread.sleep(delay * 1000);
                                continue;
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                return ResponseEntity.status(500).body("Retry interrupted");
                            }
                        }
                        return ResponseEntity.status(503).body("Imgur upload failed: RST_STREAM after max retries");
                    }
                    logger.error("Error uploading image to Imgur: {}", errorMsg);
                    return ResponseEntity.status(500).body("Imgur upload failed: " + errorMsg);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return ResponseEntity.status(500).body("Upload interrupted");
                } catch (Exception e) {
                    logger.error("Unexpected error: {}", e.getMessage());
                    return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
                }
            }
            return ResponseEntity.status(503).body("Max retries exceeded due to RST_STREAM");
    }

//        try {
//            // Send request to Imgur (Async)
//            Mono<String> response = webClient.post()
//                    .uri("/image")
//                    .header("Authorization", "Client-ID " + ImgurClientId)
//                    .bodyValue(file.getBytes())
//                    .retrieve()
//                    .bodyToMono(String.class);
//
//            // Asynchronously handle the response
//            response.subscribe(res -> {
//                JSONObject jsonObject = new JSONObject(res);
//                String imageUrl = jsonObject.getJSONObject("data").getString("link");
//
//                // Save image URL to user profile
//                User user = (User) connectedUser.getPrincipal();
//                user.setPicUrl(imageUrl);
//                userRepo.save(user);
//
//                logger.info("Profile picture uploaded successfully for user: {}", user.getEmail());
//            }, error -> {
//                logger.error("Error uploading image to Imgur: {}", error.getMessage());
//
//            });
//            //if you want to return the link
////            User user=(User) connectedUser.getPrincipal();
////            User u=userRepo.findByUserEmail(user.getEmail());
////            return ResponseEntity.ok(u.getPicUrl());
//            // Immediately return a response without waiting for Imgur
//            return ResponseEntity.ok("any string");
//
//
//        } catch (Exception e) {
//            logger.error("Unexpected error: {}", e.getMessage());
//            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
//        }
    }





