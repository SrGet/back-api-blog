package com.api.blog.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String serverUrl;
    private String realmName;
    private String realmMaster;
    private String clientSecret;
    private String clientCli;
    private String adminUsername;
    private String adminPassword;

}
