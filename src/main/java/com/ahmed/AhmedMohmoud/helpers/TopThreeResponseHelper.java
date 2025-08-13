package com.ahmed.AhmedMohmoud.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopThreeResponseHelper {
    private Integer rank;
    private Integer id;
    private Long messageCount;
    private String name;
    private String bio;
    private String picUrl;
}
