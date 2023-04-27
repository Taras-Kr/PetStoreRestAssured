package com.krasitskyi.pet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.SoftAssertions;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class PetAPITest {
    private RequestSpecification requestSpecification;
    private int petId;
    private final SoftAssertions softAssert = new SoftAssertions();

    @BeforeTest
    public void buildRequestSpecification() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .setContentType(ContentType.JSON)
                .setBasePath("/v2/pet/")
                .build();
    }

    @BeforeTest
    public void setFilter() {
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    @Description("Verifies API find pet by Id: GET https://petstore.swagger.io/v2/pet/findByStatus")
    public void verifyGetPetsByStatus() throws JsonProcessingException {

        String paramStatus = "sold";
        Response resp = given()
                .spec(requestSpecification)
                .param("status", paramStatus)
                .when()
                .get("findByStatus");
        resp
                .then()
                .statusLine("HTTP/1.1 200 OK")
                .contentType("application/json")
                .statusCode(200);

        String responseBody = resp.body().asString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jNode = objectMapper.readTree(responseBody);
        for (int i = 0; i < jNode.size(); i++) {
            softAssert.assertThat(jNode.get(i).get("status").asText())
                    .as("Pet id: " + jNode.get(i).get("id").asText() + ": " + "Status should be equal " + paramStatus)
                    .isEqualTo(paramStatus);
        }
        softAssert.assertAll();
    }

    @Test
    @Description("Verifies API Add a new pet to the store using Jackson: POST https://petstore.swagger.io/v2/pet")
    public void verifyCreatePetWithJacksonObj() throws JsonProcessingException {
        petId = PetGenerator.getRandomId(100000000, 999999999);
        String petPlainJsonObject = PetGenerator.getPetPlainJsonObject(petId);

        Response response = given()
                .spec(requestSpecification)
                .body(petPlainJsonObject)
                .when()
                .post();

        Pet expectedPet = PetGenerator.getPetFromPlainJsonObject(petPlainJsonObject);
        ValidatableResponse validatableResponse = response
                .then()
                .body("id", Matchers.is(expectedPet.getId()))
                .body("category.id", Matchers.is(expectedPet.getCategory().getId()))
                .body("category.name", Matchers.is(expectedPet.getCategory().getName()))
                .body("photoUrls", Matchers.is(expectedPet.getPhotoUrls()))
                .body("status", Matchers.is(expectedPet.getStatus()))
                .statusCode(200);

        for (int i = 0; i < expectedPet.getTags().size(); i++) {
            validatableResponse
                    .body("tags[" + i + "].id", Matchers.equalTo(expectedPet.getTags().get(i).getId()))
                    .body("tags[" + i + "].name", Matchers.equalTo(expectedPet.getTags().get(i).getName()));
        }
    }

    @Test
    @Description("Verifies API find pet by Id: GET https://petstore.swagger.io/v2/pet/{petID}")
    public void verifyGetPetById() {

        given()
                .spec(requestSpecification)
                .when()
                .get(String.valueOf(petId))
                .then()
                .statusLine("HTTP/1.1 200 OK")
                .contentType("application/json")
                .statusCode(200)
                .body("id", Matchers.is(petId));
    }

    @Test
    @Description("Verifies API Update an existing pet using Jackson: PUT https://petstore.swagger.io/v2/pet/ ")
    public void verifyUpdateExistingPetByIdWithJacksonObj() throws JsonProcessingException {

        String petPlainJsonObject = PetGenerator.getPetPlainJsonObject(petId);
        Pet expectedPet = PetGenerator.getPetFromPlainJsonObject(petPlainJsonObject);

        given()
                .spec(requestSpecification)
                .body(petPlainJsonObject)
                .when()
                .put()
                .then()
                .statusLine("HTTP/1.1 200 OK")
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("name", Matchers.is(expectedPet.getName()))
                .body("category.name", Matchers.is(expectedPet.getCategory().getName()));
    }

    @Test
    @Description("Verifies API Update an existing pet using form data: PUT https://petstore.swagger.io/v2/pet/{petId}")
    public void verifyUpdateExistingPetWithFormData() {

        String updatedName = "KittyCat UP";
        String updatedStatus = "available";
        given()
                .spec(requestSpecification)
                .contentType("application/x-www-form-urlencoded")
                .formParam("name", updatedName)
                .formParam("status", updatedStatus)
                .when()
                .post(String.valueOf(petId))
                .then()

                .statusLine("HTTP/1.1 200 OK")
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("code", Matchers.is(200))
                .body("message", Matchers.is(String.valueOf(petId)));
    }

    @Test
    @Description("Verifies API Delete pet: DELETE https://petstore.swagger.io/v2/pet/{petID}")
    public void verifyDeletePetById() {

        Response response = given()
                .spec(requestSpecification)
                .when()
                .delete(String.valueOf(petId));
        JsonPath jsonPath = response.jsonPath();

        int code = jsonPath.get("code");
        softAssert.assertThat(code)
                .as("Code should be equal 200")
                .isEqualTo(200);

        String message = jsonPath.get("message");
        softAssert.assertThat(message)
                .as("Message should be equal " + "petId")
                .isEqualTo(String.valueOf(petId));
        softAssert.assertAll();

        response.then()
                .body("code", Matchers.is(200))
                .body("message", Matchers.is(String.valueOf(petId)));
    }

    @Test
    @Description("Verifies API Add a new pet to the store using POJO: POST https://petstore.swagger.io/v2/pet")
    public void verifyCreatePetWithPOJO() {

        petId = PetGenerator.getRandomId(100000000, 999999999);
        Pet pet = PetGenerator.getPetObject(petId);
        Response response = given()
                .spec(requestSpecification)
                .body(pet)
                .when()
                .post();
        ValidatableResponse validatableResponse = response
                .then()
                .statusLine("HTTP/1.1 200 OK")
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("id", Matchers.is(pet.getId()))
                .body("category.id", Matchers.is(pet.getCategory().getId()))
                .body("category.name", Matchers.is(pet.getCategory().getName()))
                .body("photoUrls", Matchers.is(pet.getPhotoUrls()))
                .body("status", Matchers.is(pet.getStatus()));

        for (int i = 0; i < pet.getTags().size(); i++) {
            validatableResponse
                    .body("tags[" + i + "].id", Matchers.equalTo(pet.getTags().get(i).getId()))
                    .body("tags[" + i + "].name", Matchers.equalTo(pet.getTags().get(i).getName()));
        }
    }
}
