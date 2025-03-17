import com.github.javafaker.Faker;
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
    public void testUpdateUserEmailWithAuthorization() {
        // Обновление токена перед запросом
        String freshToken = userApi.loginUser(testUser).jsonPath().getString("accessToken");

        UserModel updatedUser = new UserModel(
                "new-email@test.com",
                testUser.getPassword(),
                testUser.getName()
        );

        Response response = userApi.updateUser(freshToken, updatedUser);
        assertEquals(200, response.getStatusCode());
        assertEquals("new-email@test.com", response.jsonPath().getString("user.email"));
    }

    @Test
    public void testUpdateUserWithoutAuthorization() {
        UserModel updatedUser = new UserModel("new@test.com", "pass", "Name");
        Response response = userApi.updateUser("", updatedUser);

        assertEquals(401, response.getStatusCode());
        assertEquals("You should be authorised", response.jsonPath().getString("message"));
    }
}