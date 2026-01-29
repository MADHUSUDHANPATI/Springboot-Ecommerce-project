package com.ecommerce.project.service;

import ch.qos.logback.classic.spi.IThrowableProxy;
import com.ecommerce.project.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImplementation implements CategoryService {


    private List<Category> categories = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public void postCategories(Category category) {
        category.setCategoryId(nextId++);
        categories.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {

        Category category= categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Category is not found"));

        categories.remove(category);
        return "Category " + categoryId + " deleted Successfully";
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {

        Optional< Category> optionalCategory= categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst();

        if(optionalCategory.isPresent()) {
            Category existingCategory = optionalCategory.get();
            existingCategory.setCategoryName(category.getCategoryName());
            return existingCategory;
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found to update");
        }
    }
}
