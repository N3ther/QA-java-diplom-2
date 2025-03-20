import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.UserApi;
import models.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserProfileTest {
    private UserApi userApi;
    private UserModel testUser;
    private String accessToken;

    @Before
    public void setUp() {
        Faker faker = new Faker();
        userApi = new UserApi();

        testUser = new UserModel(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12),
                faker.name().username()
        );

        Response registerResponse = userApi.registerUser(testUser);
        accessToken = registerResponse.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Обновление имени пользователя с авторизацией")
    @Description("Проверка успешного обновления имени пользователя при наличии авторизации")
    public void testUpdateUserNameWithAuthorization() {
        UserModel updatedUser = new UserModel(
                testUser.getEmail(),
                testUser.getPassword(),
                "NewUsername"
        );

        Response response = userApi.updateUser(accessToken, updatedUser);
        assertEquals(200, response.getStatusCode());
        assertEquals("NewUsername", response.jsonPath().getString("user.name"));
    }

    @Test
    @DisplayName("Обновление email пользователя с авторизацией")
    @Description("Проверка успешного обновления email пользователя при наличии авторизации")
    public void testUpdateUserEmailWithAuthorization() {
        // Обновление токена перед запросом
        String freshToken = userApi.loginUser(testUser).jsonPath().getString("accessToken");

        UserModel updatedUser = new UserModel(
                "newemailpinki@test.com",
                testUser.getPassword(),
                testUser.getName()
        );

        Response response = userApi.updateUser(freshToken, updatedUser);
        assertEquals(200, response.getStatusCode());
        assertEquals("newemailpinki@test.com", response.jsonPath().getString("user.email"));
    }

    @Test
    @DisplayName("Обновление пользователя без авторизации")
    @Description("Проверка, что обновление пользователя без авторизации возвращает ошибку 401")
    public void testUpdateUserWithoutAuthorization() {
        UserModel updatedUser = new UserModel("new@test.com", "pass", "Name");
        Response response = userApi.updateUser("", updatedUser);

        assertEquals(401, response.getStatusCode());
        assertEquals("You should be authorised", response.jsonPath().getString("message"));
    }
}