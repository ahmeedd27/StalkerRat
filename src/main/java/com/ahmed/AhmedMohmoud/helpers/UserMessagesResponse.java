package com.ahmed.AhmedMohmoud.helpers;

import com.ahmed.AhmedMohmoud.entities.Message;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMessagesResponse {
   private Page<Message> sentMessages;
    private Page<Message> receivedMessages;
}
