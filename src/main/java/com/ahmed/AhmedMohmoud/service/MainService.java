package com.ahmed.AhmedMohmoud.service;

import com.ahmed.AhmedMohmoud.dao.MessageRepo;
import com.ahmed.AhmedMohmoud.dao.UserRepo;
import com.ahmed.AhmedMohmoud.entities.Message;
import com.ahmed.AhmedMohmoud.entities.User;
import com.ahmed.AhmedMohmoud.exception.OperationNotPermittedException;
import com.ahmed.AhmedMohmoud.exception.ResourceNotFoundException;
import com.ahmed.AhmedMohmoud.helpers.*;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MainService {

    private static final Logger logger = LoggerFactory.getLogger(MainService.class);
    private final UserRepo userRepo;
    private final MessageRepo messageRepo;
    private final Cloudinary cloudinary;


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
        User receiver = userRepo.findById(receiverId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
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

    public ResponseEntity<String> uploadImage(ImageUploadRequest image, Authentication connectedUser) throws IOException {
        User u = (User) connectedUser.getPrincipal();
        Map<String, Object> options = ObjectUtils.asMap(
                "resource_type", "image",
                "timestamp", System.currentTimeMillis() / 1000
        );
        String base64Data;
        String imgUrl = "";
        try {
            base64Data = image.getImage().split(",")[1];
            byte[] fileData = Base64.getDecoder().decode(base64Data);
            Map<?, ?> uploadResult = cloudinary.uploader().upload(fileData, options);
            imgUrl = uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid base64 or upload error", e);
        }
        u.setPicUrl(imgUrl);
        userRepo.save(u);
        return ResponseEntity.ok(imgUrl);

    }


}





