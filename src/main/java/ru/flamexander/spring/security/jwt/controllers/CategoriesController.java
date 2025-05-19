package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import ru.flamexander.spring.security.jwt.dtos.CategoriesDTO;
import ru.flamexander.spring.security.jwt.dtos.ErrorResponse;
import ru.flamexander.spring.security.jwt.entities.Categories;
import ru.flamexander.spring.security.jwt.service.CategoriesService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoriesController {
    private final CategoriesService categoryService;

    @GetMapping("/main/categories")
    public ResponseEntity<List<Categories>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categories> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ModelAndView createCategory(
            @Valid @ModelAttribute CategoriesDTO categoryDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        List<String> errors = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                errors.add(error.getDefaultMessage());
            });
            redirectAttributes.addFlashAttribute("errors", errors);
            return new ModelAndView(new RedirectView("/add-category"));
        }

        categoryService.createNewCategory(categoryDto);
        return new ModelAndView(new RedirectView("/view-all-categories"));
    }



    @PutMapping("/{id}")
    public ResponseEntity<Categories> updateCategory(@PathVariable Long id, @RequestBody CategoriesDTO categoryDto) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
//        categoryService.deleteCategory(id);
//        return ResponseEntity.noContent().build();
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/by-name/{name}")
    public ResponseEntity<Categories> getCategoryByName(@PathVariable String name) {
        return categoryService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}