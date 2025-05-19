package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.flamexander.spring.security.jwt.entities.Categories;
import ru.flamexander.spring.security.jwt.repositories.CategoryRepository;
import ru.flamexander.spring.security.jwt.dtos.CategoriesDTO;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriesService {
    private CategoryRepository categoriesRepository;

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoriesRepository = categoryRepository;
    }

    public List<Categories> findAll() {
        return categoriesRepository.findAll();
    }

    public List<Categories> getAllCategories() {
        return findAll();
    }


    public Optional<Categories> findById(Long id) {
        return categoriesRepository.findById(id);
    }

    public Optional<Categories> findByName(String name) {
        return categoriesRepository.findByTitle(name);
    }

    @Transactional
    public Categories createNewCategory(CategoriesDTO categoryDto) {
        Categories category = new Categories();
        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        return categoriesRepository.save(category);
    }

    @Transactional
    public Categories updateCategory(Long id, CategoriesDTO categoryDto) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        return categoriesRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        System.out.println("Deleting category with id: " + id); // Лог для отладки
        categoriesRepository.deleteById(id);
    }
}