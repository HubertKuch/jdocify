package pl.hubertkuch.jdocify.sample;

import pl.hubertkuch.jdocify.annotations.Documented;
import pl.hubertkuch.jdocify.annotations.DocumentedExcluded;

@Documented(name = "Registration Controller", description = "Handles user registration requests.")
public class RegistrationController {

    public boolean registerUser(String username, String password) {
        // ... implementation ...
        return true;
    }

    @DocumentedExcluded
    public void someInternalMethod() {
        // ... internal logic ...
    }
}
