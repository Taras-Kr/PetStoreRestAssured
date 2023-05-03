package com.krasitskyi.user;

public class UserGenerator {

    public static User createUser() {
        User newUser = User
                .builder()
                .id(123456L)
                .username("User_123456")
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@server.com")
                .password("password")
                .userStatus(1)
                .build();
        return newUser;
    }
}
