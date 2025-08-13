package com.ahmed.AhmedMohmoud.helpers;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CloudinaryConfig {

    @Value("${CloudName}")
    private String cloudName;

    @Value("${CloudinaryApiKey}")
    private String apiKey;

    @Value("${CloudinaryApiSecret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

}
