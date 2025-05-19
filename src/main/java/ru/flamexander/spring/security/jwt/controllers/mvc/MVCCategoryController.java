package ru.flamexander.spring.security.jwt.controllers.mvc;

import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.flamexander.spring.security.jwt.dtos.CategoriesDTO;
import ru.flamexander.spring.security.jwt.entities.Categories;
import ru.flamexander.spring.security.jwt.repositories.CategoryRepository;
import ru.flamexander.spring.security.jwt.service.CategoriesService;

import javax.validation.Valid;
import java.util.List;

@Controller
public class
MVCCategoryController {

    @Autowired
    private CategoriesService categoryService;

    @GetMapping("/view-all-categories")
    public String showCategoriesPage(Model model) {
        List<Categories> categories = categoryService.getAllCategories(); // Получаем список категорий из сервиса
        model.addAttribute("categories", categories); // Передаем список в шаблон
        return "categories/view-all-categories";
    }

    @GetMapping("/add-category")
    public String showCategoriesAddPage() {
        return "categories/add-category";
    }

    @PostMapping("/add-category")
    public String addCategory(
            @Valid @ModelAttribute CategoriesDTO categoryDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "categories/add-category";
        }

        CategoriesService categoryService = new CategoriesService(); // Или через @Autowired
        categoryService.createNewCategory(categoryDto);
        return "redirect:/categories/view-all-categories";
    }



    // POST-метод для сохранения категории
//    @PostMapping("/add-category")
//    public String createCategory(@ModelAttribute CategoriesDTO categoryDto) {
//        Categories category = new Categories();
//        category.setTitle(categoryDto.getTitle());
//        category.setDescription(categoryDto.getDescription());
//        categoryRepository.save(category);
//
//        return "redirect:/view-all-categories";
//    }
}

