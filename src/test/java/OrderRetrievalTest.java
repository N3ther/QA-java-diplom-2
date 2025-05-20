import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.OrderApi;
import models.UserApi;
import models.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class OrderRetrievalTest {
    private OrderApi orderApi;
    private UserApi userApi;
    private String accessToken;

    @Before
    public void setUp() {
        Faker faker = new Faker();
        userApi = new UserApi();

        UserModel user = new UserModel(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12),
                faker.name().username()
        );

        Response registerResponse = userApi.registerUser(user);
        accessToken = userApi.loginUser(user).jsonPath().getString("accessToken");
        orderApi = new OrderApi(accessToken);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов пользователя с авторизацией")
    @Description("Проверка успешного получения заказов пользователя при наличии авторизации")
    public void testGetUserOrdersAuthorized() {
        Response response = orderApi.getUserOrders();
        assertEquals(200, response.getStatusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    @Description("Проверка, что получение заказов без авторизации возвращает ошибку 401")
    public void testGetUserOrdersUnauthorized() {
        OrderApi unauthorizedApi = new OrderApi(null);
        Response response = unauthorizedApi.getUserOrders();

        assertEquals(401, response.getStatusCode());
        assertEquals("You should be authorised", response.jsonPath().getString("message"));
    }
}