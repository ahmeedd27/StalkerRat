package com.ahmed.AhmedMohmoud.controllers;

import com.ahmed.AhmedMohmoud.helpers.*;
import com.ahmed.AhmedMohmoud.service.MainService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile(Authentication connectedUser) {
        return mainService.getProfileDto(connectedUser);
    }
    @PostMapping(value = "/uploadpic" , consumes = "multipart/form-data")
    public ResponseEntity<String> uploadPic(

            @RequestParam("file") MultipartFile file ,
            Authentication connectedUser
    ) throws IOException {
        return mainService.uploadFile(file , connectedUser);

    }

//    @GetMapping("/download")
//    public ResponseEntity<Resource> downloadFile(@RequestParam("file") String fileName){
//
//    }

    @GetMapping("/messages")
    public ResponseEntity<UserMessagesResponse> getMessages(@RequestParam(name = "page", defaultValue = "0", required = false) int page, @RequestParam(name = "size", defaultValue = "10", required = false) int size, Authentication connectedUser) {
        return mainService.getMessages(page, size, connectedUser);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<SearchByNameHelper>> getUserByName(@PathVariable("name") String name) {
        return mainService.getUserByName(name);
    }

    @DeleteMapping("/messages/{message-id}")
    public ResponseEntity<String> deleteMessage( // the receiver only the one who can do the deletion
                                                 @PathVariable("message-id") Integer msgId, Authentication connectedUser) {
        return mainService.deleteMessage(msgId, connectedUser);
    }

    @PatchMapping("/favorite/{message-id}")
    public ResponseEntity<String> makeMessageIsFavorite( // the receiver only the one who can do the message is favorite
              @PathVariable("message-id") Integer msgId
            , Authentication connectedUser
    ) {
        return mainService.makeMessageFavorite(msgId , connectedUser);
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsDto> getUserSettings(Authentication connectedUser){
        return mainService.getUserSettings(connectedUser);
    }

    @PutMapping("/settings")
    public ResponseEntity<String> updateUserSettings(
            @RequestBody SettingsDto settingsDto ,
            Authentication connectedUser
    ){
        return mainService.updateUserSettings(settingsDto , connectedUser);
    }


    @GetMapping("/search")
    public ResponseEntity<List<TopThreeResponseHelper>> getTopThreeReceiver(){
        return mainService.getTopThreeRankedReceivers();
    }

    @PostMapping("/user/{receiver-id}")
    public ResponseEntity<String> sendMessageToUser(
            @PathVariable("receiver-id") Integer receiverId ,
            @RequestBody SendMessageRequest content

    ){
        return mainService.sendMessage( receiverId , content);
    }

}
