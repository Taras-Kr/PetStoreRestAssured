package com.krasitskyi.pet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.krasitskyi.util.DataGenerator;
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

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

public class PetAPITest {
    private RequestSpecification requestSpecification;
    String petNamesFile = "src/test/resources/input_data/pet_names.txt";
    String petCategoriesFile = "src/test/resources/input_data/pet_categories.txt";
    private int petId;
    private int categoryId;
    private SoftAssertions softAssert = new SoftAssertions();


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
    @Description("Verifies API Add a new pet to the store using Jackson: POST https://petstore.swagger.io/v2/pet")
    public void verifyCreatePetWithJacksonObj() throws JsonProcessingException {
        petId = DataGenerator.getRandomId(100000000, 999999999);

        String categoryName = DataGenerator.getRandomValueFromList(DataGenerator.getListFromFile(petCategoriesFile));
        String petName = DataGenerator.getRandomValueFromList(DataGenerator.getListFromFile(petNamesFile));
        List<String> photoUrlsList = Arrays.asList("www.url1.com", "www.url2.com", "www.url3.com");

        String petStatus = "sold";

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode petNode = objectMapper.createObjectNode();
        petNode.put("id", petId);

        categoryId = DataGenerator.getId(1, 100);

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


        Response response = given()
                .spec(requestSpecification)
                .body(petPlainJsonObject)
                .when()
                .post();

        ValidatableResponse validatableResponse = response
                .then()
                .body("id", Matchers.is(petId))
                .body("category.id", Matchers.is(categoryId))
                .body("category.name", Matchers.is(categoryName))
                .body("photoUrls", Matchers.is(photoUrlsList))
                .body("status", Matchers.is(petStatus))
                .statusCode(200);

        for (int i = 0; i < tagArrayNode.size(); i++) {
            validatableResponse
                    .body("tags[" + i + "].id", Matchers.equalTo(tagArrayNode.get(i).get("id").asInt()))
                    .body("tags[" + i + "].name", Matchers.equalTo(tagArrayNode.get(i).get("name").asText()));
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
                .log()
                .all()
                .statusLine("HTTP/1.1 200 OK")
                .contentType("application/json")
                .statusCode(200)
                .body("id", Matchers.is(petId));
    }

    @Test
    @Description("Verifies API Update an existing pet using Jackson: PUT https://petstore.swagger.io/v2/pet/ ")
    public void verifyUpdateExistingPetByIdWithJacksonObj() {
        String categoryName = "Cats Upd";
        String petName = "Kitty Cat";
        List<String> photoUrlsList = Arrays.asList("www.url1.com", "www.url2.com", "www.url3.com");
        String petStatus = "available";

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode petNode = objectMapper.createObjectNode();
        petNode.put("id", petId);

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
                .statusCode(200);
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
        categoryId = DataGenerator.getId(1, 100);
        Category category = Category.builder().id(categoryId).name("POJOCats").build();
        Tag tag1 = Tag.builder().id(1).name("tagN1").build();
        Tag tag2 = Tag.builder().id(2).name("tagN2").build();

        petId = DataGenerator.getRandomId(100000000, 999999999);
        Pet pet = Pet.builder()
                .id(petId)
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
