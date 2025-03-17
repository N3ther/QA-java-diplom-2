import com.github.javafaker.Faker;
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
    public void testLoginWithValidUser() {
        Response response = userApi.loginUser(testUser);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.jsonPath().getString("accessToken"));
    }

    @Test
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