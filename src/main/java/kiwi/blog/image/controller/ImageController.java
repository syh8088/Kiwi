package kiwi.blog.image.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kiwi.blog.image.model.entity.Image;
import kiwi.blog.image.service.ImageService;
import kiwi.blog.image.service.query.ImageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "Image", description = "이미지")
@RequestMapping(ImageController.IMAGE_API_PREFIX)
@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ImageService imageService;
    private final ImageQueryService imageQueryService;

    public static final String IMAGE_API_PREFIX = "/images";

    @GetMapping("{imageNo}")
    @ApiOperation(value = "이미지 조회", notes = "이미지를 조회합니다.")
    public ResponseEntity<Resource> getImage(@PathVariable long imageNo) {

        Image image = imageQueryService.getImage(imageNo);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(image.getType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getName() + "\"")
            .body(new ByteArrayResource(image.getData()));
    }

    @PostMapping
    @ApiOperation(value = "이미지 등록", notes = "이미지를 등록합니다.")
    public ResponseEntity<?> saveImage(@RequestPart MultipartFile multipartFile) {

        return ResponseEntity.ok(imageService.saveImage(multipartFile));
    }
}
