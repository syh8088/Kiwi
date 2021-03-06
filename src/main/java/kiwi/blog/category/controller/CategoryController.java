package kiwi.blog.category.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kiwi.blog.category.model.request.CategoriesRequest;
import kiwi.blog.category.model.request.SaveCategoryRequests;
import kiwi.blog.category.model.response.CategoriesResponse;
import kiwi.blog.category.model.response.CategoryResponse;
import kiwi.blog.category.service.CategoryService;
import kiwi.blog.category.service.query.CategoryQueryService;
import kiwi.blog.common.annotation.LoginUser;
import kiwi.blog.common.config.authentication.AppUserPrincipal;
import kiwi.blog.common.config.authentication.JwtTokenProvider;
import kiwi.blog.common.model.request.JwtUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api(tags = "Category", description = "카테고리")
@RestController
@RequiredArgsConstructor
@RequestMapping("categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryQueryService categoryQueryService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<CategoriesResponse> getCategories(
            //@LoginUser Object object,
            @ModelAttribute CategoriesRequest categoriesRequest

           // , @AuthenticationPrincipal OAuth2Authentication oAuth2Authentication
            , @LoginUser JwtUserRequest jwtUserRequest

    ) throws IOException {

        //JwtUserRequest jwtUserRequest = jwtTokenProvider.getJwtTokenByClientCredentialForUser(auth);

        CategoriesResponse response = categoryQueryService.getCategories(categoriesRequest);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("{categoryNo}")
    @ApiOperation(value = "카테고리 조회", notes = "카테고리를 조회합니다. ")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable long categoryNo) {
        CategoryResponse response = categoryQueryService.getCategoryResponse(categoryNo);

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    @ApiOperation(value = "카테고리 저장/수정", notes = "카테고리를 저장/수정합니다.")
    public ResponseEntity<?> saveCategories(@RequestBody SaveCategoryRequests saveCategoryRequests) {

        categoryService.saveCategories(saveCategoryRequests);

        return ResponseEntity.noContent().build();
    }
}
