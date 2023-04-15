package com.krasitskyi.pet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class PetAPITest {
    private RequestSpecification requestSpecification;

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
    public void verifyGetPetsByStatus() {

        String paramStatus = "sold";
        given()
                .spec(requestSpecification)
                .param("status", paramStatus)
                .when()
                .get("findByStatus")
                .then()
                .statusCode(200);
    }

    @Test
    public void verifyCreatePetWithJacksonObj() throws JsonProcessingException {
        int petId = 987654;
        String categoryName = "Cats";
        String petName = "Kitty";
        List<String> photoUrlsList = Arrays.asList("www.url1.com", "www.url2.com", "www.url3.com");
        String petStatus = "sold";

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode petNode = objectMapper.createObjectNode();
        petNode.put("id", petId);

        int categoryId = 1;

        ObjectNode categoryNode = objectMapper.createObjectNode();
        categoryNode.put("id", categoryId);
        categoryNode.put("name", categoryName);
        petNode.set("category", categoryNode);

        petNode.put("name", petName);
        petNode.putPOJO("photoUrls", photoUrlsList);

        ObjectNode tag1 = objectMapper.createObjectNode();
        tag1.put("id", 1);
        tag1.put("name", "tag1Name");
        ObjectNode tag2 = objectMapper.createObjectNode();
        tag2.put("id", 2);
        tag2.put("name", "tag2Name");
        ArrayNode tagArrayNode = objectMapper.createArrayNode();
        tagArrayNode.addAll(Arrays.asList(tag1, tag2));
        petNode.set("tags", tagArrayNode);

        // Another way to add array field to json
        // petNode.putArray("tags").addAll(Arrays.asList(tag1, tag2));

        petNode.put("status", petStatus);

        String petPlainJsonObject = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(petNode);

        given()
                .spec(requestSpecification)
                .body(petPlainJsonObject)
                .when()
                .post()
                .then()
                .statusCode(200);
    }

    @Test
    public void verifyGetPetById() {

        String petId = "987654";
        given()
                .spec(requestSpecification)
                .when()
                .get(petId)
                .then()
                .statusCode(200);
    }

    @Test
    public void verifyUpdateExistingPetByIdWithJacksonObj() {
        int petId = 987654;
        String categoryName = "Cats Upd";
        String petName = "Kitty Cat";
        List<String> photoUrlsList = Arrays.asList("www.url1.com", "www.url2.com", "www.url3.com");
        String petStatus = "available";

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode petNode = objectMapper.createObjectNode();
        petNode.put("id", petId);

        int categoryId = 1;

        ObjectNode categoryNode = objectMapper.createObjectNode();
        categoryNode.put("id", categoryId);
        categoryNode.put("name", categoryName);
        petNode.set("category", categoryNode);

        petNode.put("name", petName);
        petNode.putPOJO("photoUrls", photoUrlsList);

        ObjectNode tag1 = objectMapper.createObjectNode();
        tag1.put("id", 1);
        tag1.put("name", "tag1Name");
        ObjectNode tag2 = objectMapper.createObjectNode();
        tag2.put("id", 2);
        tag2.put("name", "tag2Name");
        ArrayNode tagArrayNode = objectMapper.createArrayNode();
        tagArrayNode.addAll(Arrays.asList(tag1, tag2));
        petNode.set("tags", tagArrayNode);

        // Another way to add array field to json
        // petNode.putArray("tags").addAll(Arrays.asList(tag1, tag2));

        petNode.put("status", petStatus);
        String petPlainJsonObject;
        try {
            petPlainJsonObject = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(petNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        given()
                .spec(requestSpecification)
                .body(petPlainJsonObject)
                .when()
                .put()
                .then()
                .statusCode(200);
    }

    @Test
    public void verifyUpdateExistingPetWithFormData() {
        int petId = 987654;
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
                .statusCode(200);
    }

    @Test
    public void verifyDeletePetById() {

        String petId = "987654";
        given()
                .spec(requestSpecification)
                .when()
                .delete(petId)
                .then()
                .statusCode(200);
    }

    @Test
    public void verifyCreatePetWithPOJO() {
        Category category = Category.builder().id(1).name("POJOCats").build();
        Tag tag1 = Tag.builder().id(1).name("tagN1").build();
        Tag tag2 = Tag.builder().id(2).name("tagN2").build();

        Pet pet = Pet.builder()
                .id(987654)
                .category(category)
                .name("KittyOBJ")
                .photoUrls(Arrays.asList("www.photo1.com", "www.photo2.com"))
                .tags(Arrays.asList(tag1, tag2))
                .status("sold")
                .build();

        given()
                .spec(requestSpecification)
                .body(pet)
                .when()
                .post()
                .then()
                .statusCode(200);
    }

}