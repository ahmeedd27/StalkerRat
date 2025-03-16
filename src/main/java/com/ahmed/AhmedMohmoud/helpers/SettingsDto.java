package com.ahmed.AhmedMohmoud.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL) // will ignore all the null values
public class SettingsDto {
    private String name;
    private String bio;
    private String picUrl;
    private Integer id;
    private LocalDate dateOfBirth;
}
