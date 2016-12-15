package ua.rd.cm.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.rd.cm.domain.User;
import ua.rd.cm.services.PhotoService;
import ua.rd.cm.services.UserService;
import ua.rd.cm.web.controller.dto.MessageDto;
import ua.rd.cm.web.controller.dto.PhotoDto;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.security.Principal;

@RestController
@RequestMapping("/api/user/current/photo")
public class PhotoController {
    private UserService userService;
    private PhotoService photoService;

    public static final long MAX_SIZE = 2097152;

    @Autowired
    public PhotoController(UserService userService, PhotoService photoService) {
        this.userService = userService;
        this.photoService = photoService;
    }

    private String getFormat(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return "";
        }
        return fileName.substring(index + 1);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity getFile(@PathVariable("id") String fileName) throws IOException {
        File file = photoService.getPhoto(fileName);
        InputStream inputStream = new FileInputStream(file);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        HttpHeaders header = new HttpHeaders();

        header.setContentType(new MediaType("image", getFormat(file.getName())));
        header.setContentLength(file.length());
        return new ResponseEntity<>(inputStreamResource, header, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity upload(PhotoDto photoDto, HttpServletRequest request) {
        MessageDto message = new MessageDto();
        HttpStatus status;
        MultipartFile file = photoDto.getFile();
        User currentUser = userService.getByEmail(request.getRemoteUser());


        if (file == null || file.isEmpty()) {
            message.setError("save");
            status = HttpStatus.BAD_REQUEST;
        } else {
            //TODO: check file for renaming another type into image type

            try {
                BufferedImage bi = ImageIO.read(file.getInputStream());
                if (bi != null) {
                    System.out.println(bi.toString());
                } else {
                    System.out.println("null");
                    message.setError("pattern");
                    status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
                    return ResponseEntity.status(status).body(message);
                }
            } catch (IOException e) {
                message.setError("save");
                status = HttpStatus.BAD_REQUEST;
                return ResponseEntity.status(status).body(message);
            }
            if (file.getSize() > MAX_SIZE) {
                message.setError("maxSize");
                status = HttpStatus.PAYLOAD_TOO_LARGE;
            } else if (!file.getOriginalFilename()
                    .matches("([^\\s]+(\\.(?i)(jp(e)?g|gif|png))$)")) {
                message.setError("pattern");
                status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            }else{
                String path = photoService.savePhoto(file, currentUser.getId()
                        .toString());

                if (path != null) {
                    currentUser.setPhoto(path);
                    userService.updateUserProfile(currentUser);

                    message.setStatus(path);
                    //message.setAnswer("api/user/current/photo/" + currentUser.getId());
                    status = HttpStatus.OK;
                } else {
                    return new ResponseEntity(HttpStatus.FORBIDDEN);
                }
            }

        }

        return ResponseEntity.status(status).body(message);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public ResponseEntity delete(HttpServletRequest request) {
        MessageDto message = new MessageDto();
        HttpStatus status;
        User currentUser = userService.getByEmail(request.getRemoteUser());

        if (photoService.deletePhoto(currentUser.getId().toString())) {
            currentUser.setPhoto(null);
            userService.updateUserProfile(currentUser);

            status = HttpStatus.OK;
        } else {
            message.setError("delete");
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(message);
    }

}