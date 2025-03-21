package com.ahmed.AhmedMohmoud.controllers;

import com.ahmed.AhmedMohmoud.helpers.*;
import com.ahmed.AhmedMohmoud.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieves the profile details of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    public ResponseEntity<ProfileDto> getProfile(Authentication connectedUser) {
        return mainService.getProfileDto(connectedUser);
    }




    @GetMapping("/messages")
    @Operation(summary = "Get user messages", description = "Retrieves paginated messages for the authenticated user.")
    @Parameter(name = "page", description = "Page number (0-based)", required = false, example = "0")
    @Parameter(name = "size", description = "Number of messages per page", required = false, example = "10")
    @ApiResponse(responseCode = "200", description = "Messages retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserMessagesResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    public ResponseEntity<UserMessagesResponse> getMessages(@RequestParam(name = "page", defaultValue = "0", required = false) int page, @RequestParam(name = "size", defaultValue = "10", required = false) int size, Authentication connectedUser) {
        return mainService.getMessages(page, size, connectedUser);
    }

    @GetMapping("/search/{name}")
    @Operation(summary = "Search users by name", description = "Returns a list of users matching the provided name.")
    @Parameter(name = "name", description = "Name to search for", required = true, example = "Ahmed")
    @ApiResponse(responseCode = "200", description = "Users found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchByNameHelper.class)))
    public ResponseEntity<List<SearchByNameHelper>> getUserByName(@PathVariable("name") String name) {
        return mainService.getUserByName(name);
    }

    @DeleteMapping("/messages/{message-id}")
    @Operation(summary = "Delete a message", description = "Deletes a message by ID. Only the receiver can delete it.")
    @Parameter(name = "message-id", description = "ID of the message to delete", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Message deleted successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden if not the receiver", content = @Content)
    public ResponseEntity<String> deleteMessage( // the receiver only the one who can do the deletion
                                                 @PathVariable("message-id") Integer msgId, Authentication connectedUser) {
        return mainService.deleteMessage(msgId, connectedUser);
    }

    @PatchMapping("/favorite/{message-id}")
    @Operation(summary = "Mark message as favorite", description = "Marks a message as favorite. Only the receiver can do this.")
    @Parameter(name = "message-id", description = "ID of the message to mark as favorite", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Message marked as favorite", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden if not the receiver", content = @Content)
    public ResponseEntity<String> makeMessageIsFavorite( // the receiver only the one who can do the message is favorite
              @PathVariable("message-id") Integer msgId
            , Authentication connectedUser
    ) {
        return mainService.makeMessageFavorite(msgId , connectedUser);
    }

    @GetMapping("/settings")
    @Operation(summary = "Get user settings", description = "Retrieves the settings of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Settings retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettingsDto.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    public ResponseEntity<SettingsDto> getUserSettings(Authentication connectedUser){
        return mainService.getUserSettings(connectedUser);
    }

    @PutMapping("/settings")
    @Operation(summary = "Update user settings", description = "Updates the settings for the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Settings updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Invalid settings data", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    public ResponseEntity<String> updateUserSettings(
            @RequestBody SettingsDto settingsDto ,
            Authentication connectedUser
    ){
        return mainService.updateUserSettings(settingsDto , connectedUser);
    }


    @GetMapping("/search")
    @Operation(summary = "Get top three receivers", description = "Returns the top three ranked message receivers.")
    @ApiResponse(responseCode = "200", description = "Top three receivers retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TopThreeResponseHelper.class)))
    public ResponseEntity<List<TopThreeResponseHelper>> getTopThreeReceiver(){
        return mainService.getTopThreeRankedReceivers();
    }

    @PostMapping("/user/{receiver-id}")
    @Operation(summary = "Send message to user", description = "Sends a message to the specified receiver by ID.")
    @Parameter(name = "receiver-id", description = "ID of the receiver", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Message sent successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Invalid message content or receiver ID", content = @Content)
    public ResponseEntity<String> sendMessageToUser(
            @PathVariable("receiver-id") Integer receiverId ,
            @RequestBody SendMessageRequest content

    ){
        return mainService.sendMessage( receiverId , content);
    }

    @Operation(
            summary = "Upload an image",
            description = "Uploads an image file and associates it with the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "500", description = "Error uploading image",
                    content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file
    , Authentication connectedUser) {
        try {
           return  mainService.uploadImage(file , connectedUser);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        }
    }

}
