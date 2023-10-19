package com.krasitskyi.pet;


import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class TestClass {

    @Test
    public void requestTest(){
        Response response = given()
                .contentType(ContentType.JSON)
                .baseUri("https://petstore.swagger.io")
                .basePath("/v2/pet/findByStatus")
                .queryParam("status", "sold")
                .when()
                .get();

        System.out.println("//----------All response-------------//");
        response.then().log().all();
        System.out.println("//-----------End All response----------");

        System.out.println("//----------Response headers-------------//");
        response.then().log().headers();
        System.out.println("//-----------End Response headers----------");

        System.out.println("//----------Response Body-------------//");
        response.then().log().body();
        System.out.println("//-----------End Response body----------");

        ValidatableResponse valResponse = response.then().statusCode(200);





    }
}
