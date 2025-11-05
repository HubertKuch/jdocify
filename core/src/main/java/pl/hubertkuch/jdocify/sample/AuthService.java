package pl.hubertkuch.jdocify.sample;

import pl.hubertkuch.jdocify.annotations.DocumentedStory;
import pl.hubertkuch.jdocify.annotations.StoryStep;

@DocumentedStory(
    name = "Authenticating a user",
    steps = {
            @StoryStep(
                    narrative = "Authenticating a user via jwt token. Creates a token that have 30min expiration time and refresh token for value defined in config `auth.token.refresh-time`",
                    methods = "login"
            ),
            @StoryStep(
                    narrative = "Register user basing on given credentials, `login` should be unique and password matches the `auth.password.regex`",
                    methods = "register"
            ),
            @StoryStep(
                    narrative = "Invalidates user tokens",
                    methods = "logout"
            ),
    }
)
public class AuthService {
    public void login() {
    }

    public void register() {
    }

    public void logout() {
    }
}
