package com.howtodoinjava;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;

public class LambdaRequestHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {

        try (CognitoIdentityProviderClient client = CognitoIdentityProviderClient.create()) {
            AdminAddUserToGroupRequest adminAddUserToGroupRequest = AdminAddUserToGroupRequest.builder()
                    .groupName("USER")
                    .username(event.get("userName").toString())
                    .userPoolId(event.get("userPoolId").toString())
                    .build();

            client.adminAddUserToGroup(adminAddUserToGroupRequest);

            context.getLogger().log("User " + event.get("userName").toString() + " Added to group USER");
        } catch (Exception exception) {
            context.getLogger().log("Failed to add user " + event.get("userName").toString() + "Reason: " + exception.getMessage());
        }
        return event;
    }
}