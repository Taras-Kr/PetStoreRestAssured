package com.krasitskyi.user;

import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class UserAPITest {

    private RequestSpecification requestSpecification;
    private User user;

    @BeforeTest
    public void buildRequestSpecification() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .setContentType(ContentType.JSON)
                .setBasePath("/v2/user/")
                .build();
    }

    @BeforeTest
    public void setFilter() {
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    @Description("Verifies API create new user with correct data")
    public void verifyCreateUserPositive() {

        user = UserGenerator.createUser();
        given()
                .spec(requestSpecification)
                .body(user)
                .when()
                .post()
                .then()
                .statusLine("HTTP/1.1 200 OK")
                .contentType(ContentType.JSON)
                .body("code", Matchers.is(200))
                .body("message", Matchers.is(String.valueOf(user.getId())))
                .statusCode(200);
    }

    @Test
    @Description("Verifies API get user by username with unavailable username returns expected response with code 404")
    public void verifyGetUserByUserNameNegative() {

        given()
                .spec(requestSpecification)
                .when()
                .get("unavailableUserName")
                .then()
                .statusLine("HTTP/1.1 404 Not Found")
                .contentType(ContentType.JSON)
                .statusCode(404)
                .body("code", Matchers.is(1))
                .body("type", Matchers.is("error"))
                .body("message", Matchers.is("User not found"));
    }

    @Test
    @Description("Verifies API get user by username with available username returns expected response with code 200")
    public void verifyGetUserByUserNamePositive() {

        given().
                spec(requestSpecification)
                .when()
                .get(user.getUsername())
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200);
    }

    @Test
    @Description("Verifies API delete user by username with unavailable username returns expected response with code 404")
    public void verifyDeleteUserByUserNameNegative() {

        given()
                .spec(requestSpecification)
                .when()
                .delete("unavailableUserName")
                .then()
                .statusLine("HTTP/1.1 404 Not Found")
                .statusCode(404);
    }

    @Test
    @Description("Verifies API delete user by username with available username returns expected response with code 200")
    public void verifyDeleteUserByUserNamePositive() {

        verifyCreateUserPositive();
        given()
                .spec(requestSpecification)
                .when()
                .delete(user.getUsername())
                .then()
                .statusLine("HTTP/1.1 200 OK")
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("code", Matchers.is(200))
                .body("message", Matchers.is(user.getUsername()));
    }

}
