package com.constructflow.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private final Cors cors = new Cors();
    private final Status status = new Status();

    @Getter @Setter
    public static class Cors {
        private String[] allowedOrigins = {"http://localhost:3000", "http://localhost:3001"};
    }

    @Getter @Setter
    public static class Status {
        private String active = "Active";
        private String inProgress = "In Progress";
        private String completed = "Completed";
    }
}
