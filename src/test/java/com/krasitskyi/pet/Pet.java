package com.krasitskyi.pet;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Pet {
    private int id;
    private Category category;
    private String name;
    List<String> photoUrls;
    List <Tag> tags;
    String status;
}

@Data
@Builder
 class Category{
    private int id;
    private String name;
}

@Data
@Builder
class Tag{
    private int id;
    private String name;
}

