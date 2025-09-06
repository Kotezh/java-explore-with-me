package ewm.categories.controller;

import ewm.categories.dto.CategoryDto;
import ewm.categories.service.CategoryService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Запрос категорий from={}, size={}", from, size);
        return service.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Запрос категории по catId={}", catId);
        return service.getById(catId);
    }
}
