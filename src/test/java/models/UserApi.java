package models;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserApi {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/auth/";

    public Response registerUser(UserModel user) {
        return RestAssured.given()
                .contentType("application/json")
                .body(user)
                .post(BASE_URL + "register");
    }

    public Response loginUser(UserModel user) {
        return RestAssured.given()
                .contentType("application/json")
                .body(user)
                .post(BASE_URL + "login");
    }

    public Response updateUser(String token, UserModel user) {
        return RestAssured.given()
                .contentType("application/json")
                .auth().oauth2(token)
                .body(user)
                .patch(BASE_URL + "user");
    }

    public Response deleteUser(String token) {
        return RestAssured.given()
                .auth().oauth2(token)
                .delete(BASE_URL + "user");
    }
}