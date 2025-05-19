package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.flamexander.spring.security.jwt.entities.Product;
import ru.flamexander.spring.security.jwt.repositories.CartItemRepository;
import ru.flamexander.spring.security.jwt.repositories.ProductRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class ProductController {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public ProductController(ProductRepository productRepository,
                             CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @GetMapping("/api/catalog")
    public String showProductCatalog(@RequestParam(required = false) String category,
                                     @RequestParam(required = false) String search,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "9") int size,
                                     Model model) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productPage;

        if (category != null && !category.isEmpty()) {
            productPage = productRepository.findByCategory(category, pageable);
        } else if (search != null && !search.isEmpty()) {
            productPage = productRepository.searchProducts(search, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("pageSize", size);

        return "catalog";
    }

    @GetMapping("/add-product")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    @PostMapping("/add-product")
    @Transactional
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("image") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        try {
            if (!file.isEmpty()) {
                String imagePath = saveUploadedFile(file);
                product.setImagePath(imagePath);
            }

            productRepository.save(product);
            redirectAttributes.addFlashAttribute("success", "Продукт успешно добавлен!");
            return "redirect:/catalog";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при загрузке изображения");
            return "redirect:/add-product";
        }
    }

    @GetMapping("/edit-product/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "edit-product";
    }

    @PostMapping("/update-product/{id}")
    @Transactional
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Product product,
                                @RequestParam(value = "image", required = false) MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        try {
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setCategory(product.getCategory());

            if (file != null && !file.isEmpty()) {
                if (existingProduct.getImagePath() != null) {
                    deleteImageFile(existingProduct.getImagePath());
                }
                String imagePath = saveUploadedFile(file);
                existingProduct.setImagePath(imagePath);
            }

            productRepository.save(existingProduct);
            redirectAttributes.addFlashAttribute("success", "Товар успешно обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении товара: " + e.getMessage());
        }
        return "redirect:/catalog";
    }

    @PostMapping("/delete-product/{id}")
    @Transactional
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cartItemRepository.deleteByProductId(id);

            Product product = productRepository.findById(id).orElse(null);
            if (product != null && product.getImagePath() != null) {
                try {
                    String imagePath = product.getImagePath().replace("/uploads/", "");
                    Path fileToDelete = Paths.get(uploadPath, imagePath);
                    Files.deleteIfExists(fileToDelete);
                } catch (IOException e) {
                    System.err.println("Ошибка при удалении файла изображения: " + e.getMessage());
                }
            }

            productRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Товар успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении товара: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/catalog";
    }

    private void deleteImageFile(String imagePath) throws IOException {
        if (imagePath != null && imagePath.startsWith("/uploads/")) {
            String filename = imagePath.replace("/uploads/", "");
            Path filePath = Paths.get(uploadPath, filename);
            Files.deleteIfExists(filePath);
        }
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + file.getOriginalFilename();

        Files.copy(file.getInputStream(), uploadDir.resolve(resultFilename));
        return "/uploads/" + resultFilename;
    }
}