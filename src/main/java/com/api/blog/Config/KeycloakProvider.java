package com.api.blog.Config;


import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KeycloakProvider {

    private final KeycloakProperties keycloakProperties;

    public RealmResource getRealResource(){

        Keycloak keycloak = KeycloakBuilder.builder()

                .serverUrl(keycloakProperties.getServerUrl())
                .realm(keycloakProperties.getRealmMaster())
                .clientId(keycloakProperties.getClientCli())
                .username(keycloakProperties.getAdminUsername())
                .password(keycloakProperties.getAdminPassword())
                .clientSecret(keycloakProperties.getAdminPassword())
                .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
                .build();

        return keycloak.realm(keycloakProperties.getRealmName());

    }

    public UsersResource getUsersResource(){
        RealmResource resource = getRealResource();
        return resource.users();
    }




}
