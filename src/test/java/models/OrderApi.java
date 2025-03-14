package models;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class OrderApi {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/auth/";
    private String accessToken;

    public OrderApi(String accessToken) {
        this.accessToken = accessToken;
    }

    public Response createOrder(OrderModel order) {
        return RestAssured.given()
                .header("Authorization", accessToken)
                .contentType("application/json")
                .body(order.toJson())
                .post(BASE_URL + "/orders");
    }

    public Response getUserOrders() {
        return RestAssured.given()
                .header("Authorization", accessToken)
                .get(BASE_URL + "/orders/user");
    }

    public Response deleteOrder(String orderId) {
        return RestAssured.given()
                .header("Authorization", accessToken)
                .delete(BASE_URL + "/" + orderId);
    }
}