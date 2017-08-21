package ua.rd.cm.web.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.rd.cm.domain.Role;
import ua.rd.cm.domain.User;
import ua.rd.cm.dto.RegistrationDto;
import ua.rd.cm.services.UserInfoService;
import ua.rd.cm.services.UserService;
import ua.rd.cm.dto.MessageDto;
import ua.rd.cm.dto.UserBasicDto;
import ua.rd.cm.dto.UserDto;
import ua.rd.cm.services.exception.EmailAlreadyExistsException;
import ua.rd.cm.services.exception.PasswordMismatchException;
import ua.rd.cm.services.exception.NoSuchUserException;
import ua.rd.cm.services.exception.WrongRoleException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@AllArgsConstructor(onConstructor = @__({@Autowired}))
@RestController
@RequestMapping("/api/user")
@Log4j
public class UserController {
    private final UserService userService;
    private final UserInfoService userInfoService;



    @PreAuthorize("hasRole(\"ADMIN\")")
    @PostMapping("/create")
    public ResponseEntity registerByAdmin(@Valid @RequestBody RegistrationDto dto,
                                          BindingResult bindingResult,
                                          HttpServletRequest request
    ) {
        try {
            userService.checkUserRegistrationByAdmin(dto);
            dto.setUserStatus(User.UserStatus.CONFIRMED);
            return processUserRegistration(dto, bindingResult, request);
        } catch (WrongRoleException ex) {
            MessageDto message = new MessageDto();
            message.setError(ex.getMessage());
            return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable("id") Long userId, HttpServletRequest request) {
        MessageDto message = new MessageDto();
        if (!request.isUserInRole("ORGANISER")) {
            message.setError("unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }

        UserDto userDto = userService.getUserDtoById(userId);
        // userDto.setContactTypeService(contactTypeService);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    private ResponseEntity processUserRegistration(RegistrationDto dto, BindingResult bindingResult, HttpServletRequest request) {
        HttpStatus status;
        MessageDto message = new MessageDto();

        try{

            if (bindingResult.hasFieldErrors()) {
                status = HttpStatus.BAD_REQUEST;
                message.setError("empty_fields");
                log.error("Request for [api/user] is failed: validation is failed. [HttpServletRequest: " + request.toString() + "]");
            } else {
                userService.checkUserRegistration(dto);
                userService.registerNewUser(dto);
                status = HttpStatus.ACCEPTED;
                message.setResult("success");
            }
        } catch (PasswordMismatchException ex) {
            status = HttpStatus.BAD_REQUEST;
            message.setError(ex.getMessage());
            log.error("Request for [api/user] is failed: validation is failed. [HttpServletRequest: " + request.toString() + "]");
        } catch (EmailAlreadyExistsException ex) {
            status = HttpStatus.CONFLICT;
            message.setError(ex.getMessage());
            log.error("Registration failed: " + dto.toString() +
                    ". Email '" + dto.getEmail() + "' is already in use. [HttpServletRequest: " + request.toString() + "]");
        }

        return ResponseEntity.status(status).body(message);
    }
}