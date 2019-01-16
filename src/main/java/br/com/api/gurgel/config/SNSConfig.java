package br.com.api.gurgel.config;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SNSConfig {

    private ProfileCredentialsProvider profileCredentialsProvider() {
        return new ProfileCredentialsProvider();
    }

    @Bean
    public AmazonSNS amazonSNS() {
        return AmazonSNSClientBuilder.standard()
                .withCredentials(profileCredentialsProvider())
                .withRegion(Regions.US_EAST_1)
                .build();
    }

}
