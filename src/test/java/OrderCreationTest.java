import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.OrderApi;
import models.OrderModel;
import models.UserApi;
import models.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class OrderCreationTest {
    private OrderApi orderApi;
    private UserApi userApi;
    private String accessToken;
    private Faker faker;

    @Before
    public void setUp() {
        faker = new Faker();
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
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка успешного создания заказа при наличии авторизации")
    public void testCreateOrderWithAuthorization() {
        Response ingredientsResponse = orderApi.getIngredients();
        List<String> ingredientIds = ingredientsResponse.jsonPath().getList("data._id");
        assertFalse("Нет доступных ингредиентов", ingredientIds.isEmpty());

        OrderModel order = new OrderModel(ingredientIds.subList(0, 2).toArray(new String[0]));
        Response response = orderApi.createOrder(order);

        assertEquals(200, response.getStatusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка, что создание заказа без авторизации возвращает ошибку 401")
    public void testCreateOrderWithoutAuthorization() {
        OrderApi unauthorizedApi = new OrderApi(null);
        Response ingredientsResponse = orderApi.getIngredients();
        List<String> ingredientIds = ingredientsResponse.jsonPath().getList("data._id");

        OrderModel order = new OrderModel(ingredientIds.subList(0, 2).toArray(new String[0]));
        Response response = unauthorizedApi.createOrder(order);

        assertEquals(401, response.getStatusCode());
        assertEquals("You should be authorised", response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создание заказа с некорректным хешем")
    @Description("Проверка, что создание заказа с некорректным хешем возвращает ошибку 500")
    public void testCreateOrderWithIncorrectHash() {
        OrderModel order = new OrderModel(new String[]{"invalid_ingredient_123"});
        Response response = orderApi.createOrder(order);

        assertEquals(500, response.getStatusCode());

        String responseBody = response.getBody().asString().toLowerCase();
        assertTrue("Ответ должен содержать информацию об ошибке",
                responseBody.contains("internal server error"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка, что создание заказа без указания ингредиентов возвращает ошибку 400")
    public void testCreateOrderWithoutIngredients() {
        OrderModel order = new OrderModel(new String[]{});
        Response response = orderApi.createOrder(order);

        assertEquals(400, response.getStatusCode());
        assertEquals("Ingredient ids must be provided", response.jsonPath().getString("message"));
    }
}