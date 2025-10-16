package pl.hubertkuch.jdocify.sample;

import pl.hubertkuch.jdocify.annotations.Documented;

/**
 * A sample service for user operations.
 */
@Documented(name = "User Service")
public class UserService {

    private String databaseConnection;

    public UserService(String databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Finds a user by their ID.
     *
     * @param id The ID of the user to find.
     */
    public void findUserById(Long id) {
        // implementation details...
    }

    public void deleteUser(Long id) {
        // implementation details...
    }
}
