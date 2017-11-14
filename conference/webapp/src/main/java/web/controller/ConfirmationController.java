package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import domain.model.User;
import domain.model.VerificationToken;
import lombok.AllArgsConstructor;
import service.businesslogic.api.UserService;
import service.businesslogic.dto.MessageDto;
import service.businesslogic.impl.VerificationTokenService;
import service.infrastructure.mail.MailService;
import service.infrastructure.mail.preparator.OldEmailMessagePreparator;

@RestController
@AllArgsConstructor(onConstructor = @__({@Autowired}))
@RequestMapping("/confirmation")
public class ConfirmationController {

    private final WithTokenGetRequestProcessor withTokenGetRequestProcessor;
    private final VerificationTokenService tokenService;
    private final UserService userService;
    private final MailService mailService;

    @GetMapping("/registrationConfirm/{token}")
    public ResponseEntity<MessageDto> confirmRegistration(@PathVariable String token) {
        return withTokenGetRequestProcessor.process(token, VerificationToken.TokenType.CONFIRMATION, verificationToken -> {
            User user = verificationToken.getUser();
            setUserStatusConfirmed(user);
        });
    }

    @GetMapping("/newEmailConfirm/{token:.+}")
    public ResponseEntity<MessageDto> confirmNewEmail(@PathVariable String token) {
        return withTokenGetRequestProcessor.process(token, VerificationToken.TokenType.CHANGING_EMAIL, verificationToken -> {
            User user = verificationToken.getUser();
            String newEmail = tokenService.getEmail(token);
            String oldEmail = user.getEmail();
            user.setEmail(newEmail);
            userService.updateUserProfile(user);
            mailService.sendEmail(user, new OldEmailMessagePreparator(oldEmail));
        });
    }

    private void setUserStatusConfirmed(User user) {
        user.setStatus(User.UserStatus.CONFIRMED);
        userService.updateUserProfile(user);
    }
}