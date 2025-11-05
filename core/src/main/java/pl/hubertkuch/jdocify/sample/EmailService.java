package pl.hubertkuch.jdocify.sample;

import pl.hubertkuch.jdocify.annotations.Documented;

@Documented(name = "Email Service", description = "Handles sending various types of emails.")
public class EmailService {

    public void sendVerificationEmail(String email, String token) {
        // ... implementation ...
    }

    public void sendWelcomeEmail(String email) {
        // ... implementation ...
    }
}
