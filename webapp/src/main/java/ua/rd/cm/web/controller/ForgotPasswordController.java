package ua.rd.cm.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import ua.rd.cm.domain.User;
import ua.rd.cm.domain.VerificationToken;
import ua.rd.cm.dto.MessageDto;
import ua.rd.cm.dto.NewPasswordDto;
import ua.rd.cm.services.MailService;
import ua.rd.cm.services.UserService;
import ua.rd.cm.services.VerificationTokenService;
import ua.rd.cm.services.preparator.ForgotMessagePreparator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ForgotPasswordController {

    private final WithTokenGetRequestProcessor withTokenGetRequestProcessor;
    private final VerificationTokenService tokenService;
    private final UserService userService;
    private final MailService mailService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ForgotPasswordController(WithTokenGetRequestProcessor withTokenGetRequestProcessor,
                                    VerificationTokenService tokenService,
                                    UserService userService, MailService mailService,
                                    ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.withTokenGetRequestProcessor = withTokenGetRequestProcessor;
        this.tokenService = tokenService;
        this.userService = userService;
        this.mailService = mailService;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity forgotPassword(@RequestBody String mail, HttpServletRequest request) throws IOException {
        HttpStatus httpStatus;
        MessageDto responseMessage = new MessageDto();
        ObjectNode node = objectMapper.readValue(mail, ObjectNode.class);

        if (node.get("mail") != null) {
            if (!userService.isEmailExist(node.get("mail").textValue())) {
                httpStatus = HttpStatus.BAD_REQUEST;
                responseMessage.setError("email_not_found");
            } else {
                User currentUser = userService.getByEmail(node.get("mail").textValue());
                VerificationToken token = tokenService.createToken(currentUser, VerificationToken.TokenType.FORGOT_PASS);
                tokenService.setPreviousTokensExpired(token);
                tokenService.saveToken(token);
                mailService.sendEmail(currentUser, new ForgotMessagePreparator(token, mailService.getUrl()));
                httpStatus = HttpStatus.OK;
                responseMessage.setResult("success");
            }
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
            responseMessage.setError("email_is_empty");
        }

        return ResponseEntity.status(httpStatus).body(responseMessage);
    }

    @GetMapping("/forgotPassword/{token}")
    public ResponseEntity changePassword(@PathVariable String token) {
        return withTokenGetRequestProcessor.process(token, VerificationToken.TokenType.FORGOT_PASS, verificationToken -> {
        });
    }

    @PostMapping("/forgotPassword/{token}")
    public ResponseEntity changePassword(@PathVariable String token, @Valid @RequestBody NewPasswordDto dto, BindingResult bindingResult) {
        VerificationToken verificationToken = tokenService.getToken(token);
        if (!isPasswordConfirmed(dto))
            return ResponseEntity.badRequest().build();

        User currentUser = verificationToken.getUser();
        currentUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        userService.updateUserProfile(currentUser);

        return ResponseEntity.ok().build();
    }

    private boolean isPasswordConfirmed(NewPasswordDto dto) {
        return dto.getPassword().equals(dto.getConfirm());
    }

}