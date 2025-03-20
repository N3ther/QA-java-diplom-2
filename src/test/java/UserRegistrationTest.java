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

public class UserRegistrationTest {
    private UserApi userApi;
    private Faker faker;
    private String token;

    @Before
    public void setUp() {
        faker = new Faker();
        userApi = new UserApi();
    }

    @After
    public void tearDown() {
        if (token != null) {
            userApi.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания уникального пользователя")
    public void testCreateUniqueUser() {
        UserModel user = new UserModel(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12),
                faker.name().username()
        );

        Response response = userApi.registerUser(user);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
        this.token = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание существующего пользователя")
    @Description("Проверка, что создание уже существующего пользователя возвращает ошибку 403")
    public void testCreateExistingUser() {
        UserModel existingUser = new UserModel(
                "existing@test.com",
                "password",
                "ExistingUser"
        );
        userApi.registerUser(existingUser);

        Response response = userApi.registerUser(existingUser);
        assertEquals(403, response.getStatusCode());
        assertEquals("User already exists", response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка, что создание пользователя без имени возвращает ошибку 403")
    public void testCreateUserWithoutName() {
        UserModel user = new UserModel("no-name@test.com", "password", null);
        Response response = userApi.registerUser(user);

        assertEquals(403, response.getStatusCode());
        assertEquals("Email, password and name are required fields", response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка, что создание пользователя без email возвращает ошибку 403")
    public void testCreateUserWithoutEmail() {
        UserModel user = new UserModel(null, "password", "Username");
        Response response = userApi.registerUser(user);

        assertEquals(403, response.getStatusCode());
        assertEquals("Email, password and name are required fields", response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка, что создание пользователя без пароля возвращает ошибку 403")
    public void testCreateUserWithoutPassword() {
        UserModel user = new UserModel("no-pass@test.com", null, "Username");
        Response response = userApi.registerUser(user);

        assertEquals(403, response.getStatusCode());
        assertEquals("Email, password and name are required fields", response.jsonPath().getString("message"));
    }
}