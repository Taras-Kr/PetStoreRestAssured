package com.krasitskyi.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
    private int id;
    private Category category;
    private String name;
    private List<String> photoUrls;
    private List <Tag> tags;
    private String status;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
 class Category{
    private int id;
    private String name;

}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Tag{
    private int id;
    private String name;
}

