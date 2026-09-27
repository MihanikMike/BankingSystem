package com.mike.bank.ui.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static{

        try(InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")){

            if(input == null){
                throw new RuntimeException("config.properties was not found");
            }

            properties.load(input);
        }catch(IOException exception){

            throw new RuntimeException("Failed to load config.properties", exception);
        }
    }

    public static String get(String key){

        return properties.getProperty(key);
    }
}
