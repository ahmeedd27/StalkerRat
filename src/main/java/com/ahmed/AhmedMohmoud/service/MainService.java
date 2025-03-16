package com.ahmed.AhmedMohmoud.service;

import com.ahmed.AhmedMohmoud.dao.MessageRepo;
import com.ahmed.AhmedMohmoud.dao.UserRepo;
import com.ahmed.AhmedMohmoud.entities.Message;
import com.ahmed.AhmedMohmoud.entities.User;
import com.ahmed.AhmedMohmoud.file.FileStorageService;
import com.ahmed.AhmedMohmoud.helpers.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {
    private final UserRepo userRepo;
    private final MessageRepo messageRepo;
    private final FileStorageService fileStorageService;



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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message doesn't exist"));
        if (!m.getReceiver().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the receiver can delete the message");

        }
        int deletedRows = messageRepo.deleteSpecificMessageReceivedToSpecificUser(msgId, user.getId());
        if (deletedRows == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete message");
        }
        return ResponseEntity.ok("Deleted Successfully");
    }

    public ResponseEntity<String> makeMessageFavorite(Integer msgId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Message message = messageRepo.makeMessageFavorite(msgId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message doesn't exist"));
        if (!message.getReceiver().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the receiver can delete the message");
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
        return null;
    }

    public ResponseEntity<String> updateUserSettings(SettingsDto settingsDto, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        User existingUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("NOT FOUND"));
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
        User receiver = userRepo.findById(receiverId).orElseThrow();
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
            m.setSender(u);
        }
        messageRepo.save(m);
        return ResponseEntity.ok("Successfully saved");


    }


    public ResponseEntity<String> uploadFile(MultipartFile file, Authentication connectedUser) throws IOException {
        User user=(User) connectedUser.getPrincipal();
        User u=userRepo.findById(user.getId()).orElseThrow(() -> new NoSuchElementException("Not Found"));
        String picUrl=fileStorageService.saveFile(file , user.getId());
        if (picUrl == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file");
        }
        u.setPicUrl(picUrl);
        userRepo.save(u);
        return ResponseEntity.ok(picUrl);
    }
}
