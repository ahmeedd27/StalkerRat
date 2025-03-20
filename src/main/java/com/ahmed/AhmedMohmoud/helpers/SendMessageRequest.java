package com.ahmed.AhmedMohmoud.helpers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest {

    @NotEmpty(message = "should not being empty")
    @NotBlank(message = "not mandatory")
    private String messageContent;
}
