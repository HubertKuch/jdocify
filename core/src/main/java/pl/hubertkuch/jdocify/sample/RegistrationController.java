package pl.hubertkuch.jdocify.sample;

import pl.hubertkuch.jdocify.annotations.Documented;
import pl.hubertkuch.jdocify.annotations.DocumentedExcluded;

@Documented(name = "Registration Controller", description = "Handles user registration requests.")
public class RegistrationController {

    private EmailService emailService;

    public boolean registerUser(String username, String password) {
        makeUser();
        saveUser();
        sendVerificationEmail();
        someInternalMethod();
        return true;
    }

    private void makeUser() {}
    private void saveUser() {}

    private void sendVerificationEmail() {

    }

    @DocumentedExcluded
    public void someInternalMethod() {
        // ... internal logic ...
    }
}
