package com.libraryApp.utilities;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.Test;

public class LibraryUtils {
/*
    public static String GetTokenByRole(String role) {
        switch (role) {
            case "librarian":
                String email = ConfigurationReader.getProperty("librarian_email");
                String password = ConfigurationReader.getProperty("librarian_password");

            case "user":
                email = ConfigurationReader.getProperty("user_email");
                password = ConfigurationReader.getProperty("user_password");
        }
        return GetToken(email,password)
    }*/

    public static String GetToken(String email, String password){
        JsonPath jsonPath = RestAssured.given().log().uri()
                .accept(ContentType.JSON)
                .contentType(ContentType.URLENC)
                .formParam("email", email)
                .formParam("password", password)
                .when().post("/login")
                .then().extract().jsonPath();

        String token = jsonPath.getString("token");

        return token;
    }

}
