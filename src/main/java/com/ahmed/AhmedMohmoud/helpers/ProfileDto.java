package com.ahmed.AhmedMohmoud.helpers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private Integer id;
    private String name;
    private String bio;
    private String picUrl;
}
