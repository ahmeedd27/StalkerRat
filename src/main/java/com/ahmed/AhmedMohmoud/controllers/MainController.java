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

    @Operation(summary = "Get user profile", description = "Retrieves the profile details of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDto.class)))

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile(Authentication connectedUser) {
        return mainService.getProfileDto(connectedUser);
    }

    @Operation(summary = "Get user messages", description = "Retrieves paginated messages for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Messages retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserMessagesResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content)
    })
    @GetMapping("/messages")
    public ResponseEntity<UserMessagesResponse> getMessages(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,

            @Parameter(description = "Number of messages per page", example = "10")
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,

            Authentication connectedUser) {
        return mainService.getMessages(page, size, connectedUser);
    }


    @Operation(summary = "Search users by name", description = "Returns a list of users matching the provided name.")
    @ApiResponse(responseCode = "200", description = "Users found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchByNameHelper.class)))

    @GetMapping("/search/{name}")
    public ResponseEntity<List<SearchByNameHelper>> getUserByName(@PathVariable("name") String name) {
        return mainService.getUserByName(name);
    }

    @Operation(summary = "Delete a message", description = "Deletes a message by ID. Only the receiver can delete it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden if not the receiver")
    })

    @DeleteMapping("/messages/{message-id}")
    public ResponseEntity<String> deleteMessage( // the receiver only the one who can do the deletion
                                                 @PathVariable("message-id") Integer msgId, Authentication connectedUser) {
        return mainService.deleteMessage(msgId, connectedUser);
    }


    @Operation(summary = "Mark message as favorite", description = "Marks a message as favorite. Only the receiver can do this.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message marked as favorite"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden if not the receiver")
    })
    @PatchMapping("/favorite/{message-id}")
    public ResponseEntity<String> makeMessageIsFavorite( // the receiver only the one who can do the message is favorite
                                                         @PathVariable("message-id") Integer msgId
            , Authentication connectedUser
    ) {
        return mainService.makeMessageFavorite(msgId, connectedUser);
    }

    @Operation(summary = "Get user settings", description = "Retrieves the settings of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Settings retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SettingsDto.class)))
    @GetMapping("/settings")
    public ResponseEntity<SettingsDto> getUserSettings(Authentication connectedUser) {
        return mainService.getUserSettings(connectedUser);
    }

    @Operation(summary = "Update user settings", description = "Updates the settings for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Settings updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid settings data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PutMapping("/settings")
    public ResponseEntity<String> updateUserSettings(
            @RequestBody SettingsDto settingsDto,
            Authentication connectedUser
    ) {
        return mainService.updateUserSettings(settingsDto, connectedUser);
    }


    @Operation(summary = "Get top three receivers", description = "Returns the top three ranked message receivers.")
    @ApiResponse(responseCode = "200", description = "Top three receivers retrieved",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TopThreeResponseHelper.class)))
    @GetMapping("/search")
    public ResponseEntity<List<TopThreeResponseHelper>> getTopThreeReceiver() {
        return mainService.getTopThreeRankedReceivers();
    }

    @Operation(summary = "Send message to user", description = "Sends a message to the specified receiver by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid message content or receiver ID")
    })
    @PostMapping("/user/{receiver-id}")
    public ResponseEntity<String> sendMessageToUser(
            @PathVariable("receiver-id") Integer receiverId,
            @RequestBody SendMessageRequest content

    ) {
        return mainService.sendMessage(receiverId, content);
    }

    @Operation(
            summary = "Upload an image",
            description = "Uploads an image file and associates it with the authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Upload an image file",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format"),
            @ApiResponse(responseCode = "500", description = "Error uploading image")
    })
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImage(
            @Parameter(description = "Image file to upload", required = true)
            @RequestParam("file") MultipartFile file,

            Authentication connectedUser
    ) {
        try {
            return mainService.uploadImage(file, connectedUser);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        }
    }

}
