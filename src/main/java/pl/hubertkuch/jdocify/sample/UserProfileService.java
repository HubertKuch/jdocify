package pl.hubertkuch.jdocify.sample;

import pl.hubertkuch.jdocify.annotations.Documented;

@Documented(name = "User Profile Service", description = "Manages user profile information.")
public class UserProfileService {

    public boolean updateProfile(String userId, String newProfileData) {
        // ... implementation ...
        return true;
    }
}
