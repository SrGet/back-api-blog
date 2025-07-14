package com.api.blog.Service;

import com.api.blog.Config.KeycloakProvider;
import com.api.blog.DTOs.UserDto;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakProvider keycloakProvider;

    public String create(UserDto userDTO){

        int status;

        UsersResource usersResource = keycloakProvider.getUsersResource();
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setFirstName(userDTO.getName());
        userRepresentation.setUsername(userDTO.getUsername());
        userRepresentation.setLastName(userDTO.getLastName());
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);

        Response response = usersResource.create(userRepresentation);

        status = response.getStatus();

        if(status == 201){

            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/")+1);

            setPasswordOnKeycloak(usersResource, userDTO.getPassword(), userId);
            setRoleOnKeycloak(userId);

            return userId;

        }else {
            return null;
        }
    }

    private void setPasswordOnKeycloak(UsersResource usersResource, String password, String userId){
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);

        usersResource.get(userId).resetPassword(credentialRepresentation);
    }

    private void setRoleOnKeycloak(String userId){

        RealmResource realmResource = keycloakProvider.getRealResource();

        List<RoleRepresentation> roleRepresentation = List.of(realmResource.roles().get("user").toRepresentation());

        realmResource.users().get(userId).roles().realmLevel().add(roleRepresentation);

    }

    public void delete(String userId) {

        try {
            keycloakProvider.getUsersResource().get(userId).remove();
        } catch (Exception e) {
            System.out.println("No se pudo eliminar el usuario: " + userId);
        }
    }

}
