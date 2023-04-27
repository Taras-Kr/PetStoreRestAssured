package com.krasitskyi.pet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PetGenerator {

    static String petNamesFile = "src/test/resources/input_data/pet_names.txt";
    static String petCategoriesFile = "src/test/resources/input_data/pet_categories.txt";

    public static int getId(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static int getRandomId(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }

    public static List<String> getListFromFile(String file) {
        List<String> dataFromFile = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (bufferedReader.ready()) {
                dataFromFile.add(bufferedReader.readLine());
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dataFromFile;
    }

    public static String getRandomValueFromList(List<String> values) {
        return values.get(getId(0, values.size() - 1));
    }

    public static String getPetPlainJsonObject(int petId) throws JsonProcessingException {

        String categoryName = PetGenerator.getRandomValueFromList(PetGenerator.getListFromFile(petCategoriesFile));
        String petName = PetGenerator.getRandomValueFromList(PetGenerator.getListFromFile(petNamesFile));
        List<String> photoUrlsList = Arrays.asList("www.url1.com", "www.url2.com", "www.url3.com");
        String petStatus = "sold";

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode petNode = objectMapper.createObjectNode();
        petNode.put("id", petId);

        int categoryId = PetGenerator.getId(1, 100);

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

        String petPlainJsonObj = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(petNode);
        return petPlainJsonObj;
    }

    public static Pet getPetFromPlainJsonObject(String petJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(petJson, Pet.class);
    }

    public static Pet getPetObject(int petId) {
        int categoryId = PetGenerator.getId(1, 100);
        Category category = Category.builder()
                .id(categoryId)
                .name(PetGenerator.getRandomValueFromList(PetGenerator.getListFromFile(petCategoriesFile)))
                .build();

        Tag tag1 = Tag.builder().id(1).name("tagN1").build();
        Tag tag2 = Tag.builder().id(2).name("tagN2").build();

        Pet pet = Pet.builder()
                .id(petId)
                .category(category)
                .name(PetGenerator.getRandomValueFromList(PetGenerator.getListFromFile(petNamesFile)))
                .photoUrls(Arrays.asList("www.photo1.com", "www.photo2.com"))
                .tags(Arrays.asList(tag1, tag2))
                .status("sold")
                .build();
        return pet;
    }

}


