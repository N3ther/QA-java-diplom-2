
import models.OrderApi;
import models.UserApi;
import models.OrderModel;
import io.restassured.response.Response;
import models.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderTest {
    private OrderApi orderApi;
    private UserApi userApi;     // Создайте поле для UserApi
    private String accessToken;
    private String userId;
    private String orderId;
    private String token;

    @Before
    public void setUp() {
        userApi = new UserApi();
        String userEmail = "randomewusertest@yandex.ru";
        String userPassword = "password";
        String userName = "Username";
        UserModel user = new UserModel(userEmail, userPassword, userName);

        // Подходит ли запрос на регистрацию?
        Response registerResponse = userApi.registerUser(user);
        System.out.println("Register Response Status: " + registerResponse.getStatusCode());
        System.out.println("Register Response Body: " + registerResponse.getBody().asString());
        assertEquals(200, registerResponse.getStatusCode());

        // Логин пользователя для получения токена
        Response loginResponse = userApi.loginUser(user);
        assertEquals(200, loginResponse.getStatusCode());
        accessToken = loginResponse.jsonPath().getString("accessToken");
        orderApi = new OrderApi(accessToken);
    }

    @After
    public void tearDown() {
        // Удаляем пользователя и заказы после каждого теста
        if (token != null) {
            userApi.deleteUser(token); // Удаляем пользователя после теста
        }
        if (token != null) {
            orderApi.deleteOrder(token);
        }
    }


    @Test
    public void testCreateOrderWithAuthorization() {
        OrderModel order = new OrderModel(new String[]{"ingredient1", "ingredient2"});
        Response response = orderApi.createOrder(order);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testCreateOrderWithoutAuthorization() {
        OrderApi unauthorizedApi = new OrderApi(null);
        OrderModel order = new OrderModel(new String[]{"ingredient1", "ingredient2"});
        Response response = unauthorizedApi.createOrder(order);
        assertEquals(401, response.getStatusCode());
    }

    @Test
    public void testCreateOrderWithIncorrectHash() {
        OrderModel order = new OrderModel(new String[]{"incorrectIngredient"});
        Response response = orderApi.createOrder(order);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void testGetUserOrdersAuthorized() {
        Response response = orderApi.getUserOrders();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testGetUserOrdersUnauthorized() {
        OrderApi unauthorizedApi = new OrderApi(null);
        Response response = unauthorizedApi.getUserOrders();
        assertEquals(401, response.getStatusCode());
    }
}