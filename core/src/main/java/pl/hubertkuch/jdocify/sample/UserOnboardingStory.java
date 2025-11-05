package pl.hubertkuch.jdocify.sample;

import pl.hubertkuch.jdocify.annotations.DocumentedStory;
import pl.hubertkuch.jdocify.annotations.StoryStep;

@DocumentedStory(
    name = "User Onboarding Workflow",
    steps = {
        @StoryStep(narrative = "The user first registers by submitting their details through the RegistrationController."),
        @StoryStep(element = RegistrationController.class, methods = {"registerUser"}),
        @StoryStep(narrative = "Upon registration, a verification email is sent via the EmailService."),
        @StoryStep(element = EmailService.class, methods = {"sendVerificationEmail"}),
        @StoryStep(narrative = "After email verification, the user is guided to complete their profile using the UserProfileService."),
        @StoryStep(element = UserProfileService.class, methods = {"updateProfile"})
    }
)
public class UserOnboardingStory {}
