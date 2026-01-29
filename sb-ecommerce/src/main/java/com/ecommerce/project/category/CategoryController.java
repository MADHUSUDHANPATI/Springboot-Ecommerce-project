package com.ecommerce.project.category;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired  // This is filed injection
    private CategoryService categoryService;

    // or , we can make use of constructor here for bean creation.

    @GetMapping("/public/categories")
    //@RequestMapping(value = "/api/public/categories", method = RequestMethod.GET)  -----> Both Lines are same.
    public ResponseEntity<List<Category>> getCategories() {
        return new ResponseEntity<>(categoryService.getCategories(), HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<String> postCategories(@RequestBody Category category) {
        categoryService.postCategories(category);
        return new ResponseEntity<>( " post successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory( @PathVariable Long categoryId) {
        try {
            String status = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch ( ResponseStatusException e) {

            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
            // other way
            // return ResponseEntity.ok(status); same as above
            // return ResponseEntity.status(HttpStatus.OK).body(Status); same;
        }

    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable Long categoryId,  @RequestBody Category category) {
        try {
            Category savedCategory = categoryService.updateCategory(categoryId, category);
            return new ResponseEntity<>("Category with id " +categoryId + " updated", HttpStatus.OK);
        }
        catch ( ResponseStatusException e) {

            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }
}
