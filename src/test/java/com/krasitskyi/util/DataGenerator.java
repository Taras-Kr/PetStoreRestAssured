package com.krasitskyi.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {

    public static int getId(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public int getRandomId(int min, int max) {
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
}
