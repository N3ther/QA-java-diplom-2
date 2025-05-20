package models;


import config.RestClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserApi {

    @Step("Регистрация пользователя: email={user.email}, имя={user.name}")
    public Response registerUser(UserModel user) {
        return given()
                .spec(RestClient.getBaseSpec())
                .body(user)
                .post("auth/register");
    }

    @Step("Авторизация пользователя: email={user.email}")
    public Response loginUser(UserModel user) {
        return given()
                .spec(RestClient.getBaseSpec())
                .body(user)
                .post("auth/login");
    }

    @Step("Обновление данных пользователя")
    public Response updateUser(String token, UserModel user) {
        return given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", token)
                .body(user)
                .patch("auth/user");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        return given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", token)
                .delete("auth/user");
    }
}