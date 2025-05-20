package models;


import config.RestClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderApi {
    private final String accessToken;

    public OrderApi(String accessToken) {
        this.accessToken = accessToken != null ? accessToken : "";
    }

    @Step("Получение списка ингредиентов")
    public Response getIngredients() {
        return given()
                .spec(RestClient.getBaseSpec())
                .get("ingredients");
    }

    @Step("Создание заказа с ингредиентами: {order.ingredients}")
    public Response createOrder(OrderModel order) {
        return given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", this.accessToken)
                .body(order)
                .post("orders");
    }

    @Step("Получение заказов пользователя")
    public Response getUserOrders() {
        return given()
                .spec(RestClient.getBaseSpec())
                .header("Authorization", this.accessToken)
                .get("orders");
    }
}