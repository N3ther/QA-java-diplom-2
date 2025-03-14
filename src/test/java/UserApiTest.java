import io.restassured.response.Response;
import models.UserApi;
import models.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UserApiTest {
    private UserApi userApi;
    private UserModel testUser;
    private String token;

    @Before
    public void setUp() {
        userApi = new UserApi();
        testUser = new UserModel("testnewuser@yandex.ru", "password", "Username");
        userApi.registerUser(testUser);
    }

    @After
    public void tearDown() {
        if (token != null) {
            userApi.deleteUser(token); // Удаляем пользователя после теста
        }
    }

    @Test
    public void testCreateUniqueUser() {
        UserModel uniqueUser = new UserModel("unique-user@yandex.ru", "password", "UniqueUser");
        Response response = userApi.registerUser(uniqueUser);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testCreateExistingUser() {
        Response response = userApi.registerUser(testUser);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void testCreateUserWithMissingField() {
        UserModel userWithoutName = new UserModel("missing-name@yandex.ru", "password", null);
        Response response = userApi.registerUser(userWithoutName);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void testLoginWithValidUser() {
        Response response = userApi.loginUser(testUser);
        assertEquals(200, response.getStatusCode());
        token = response.jsonPath().getString("accessToken");
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        UserModel invalidUser = new UserModel("invalid@yandex.ru", "wrong_password", "InvalidUser");
        Response response = userApi.loginUser(invalidUser);
        assertEquals(401, response.getStatusCode());
    }

    @Test
    public void testUpdateUserWithAuthorization() {
        token = userApi.loginUser(testUser).jsonPath().getString("accessToken");
        UserModel updatedUser = new UserModel("new-email@yandex.ru", "new_password", "NewUsername");
        Response response = userApi.updateUser(token, updatedUser);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testUpdateUserWithoutAuthorization() {
        UserModel updatedUser = new UserModel("new-email@yandex.ru", "new_password", "NewUsername");
        Response response = userApi.updateUser(null, updatedUser);
        assertEquals(401, response.getStatusCode());
    }
}