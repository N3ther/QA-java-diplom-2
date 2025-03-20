import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.UserApi;
import models.UserModel;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserLoginTest {
    private UserApi userApi;
    private UserModel testUser;

    @Before
    public void setUp() {
        Faker faker = new Faker();
        userApi = new UserApi();

        testUser = new UserModel(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12),
                faker.name().username()
        );
        userApi.registerUser(testUser);
    }

    @Test
    @DisplayName("Вход с валидными данными пользователя")
    @Description("Проверка успешного входа с валидными данными пользователя")
    public void testLoginWithValidUser() {
        Response response = userApi.loginUser(testUser);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.jsonPath().getString("accessToken"));
    }

    @Test
    @DisplayName("Вход с невалидным email")
    @Description("Проверка, что вход с невалидным email возвращает ошибку 401")
    public void testLoginWithInvalidEmail() {
        UserModel invalidUser = new UserModel(
                "invalid@test.com",
                testUser.getPassword(),
                testUser.getName()
        );

        Response response = userApi.loginUser(invalidUser);
        assertEquals(401, response.getStatusCode());
        assertEquals("email or password are incorrect", response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Вход с невалидным паролем")
    @Description("Проверка, что вход с невалидным паролем возвращает ошибку 401")
    public void testLoginWithInvalidPassword() {
        UserModel invalidUser = new UserModel(
                testUser.getEmail(),
                "wrong_password",
                testUser.getName()
        );

        Response response = userApi.loginUser(invalidUser);
        assertEquals(401, response.getStatusCode());
        assertEquals("email or password are incorrect", response.jsonPath().getString("message"));
    }
}