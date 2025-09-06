package ewm.categories.controller;

import ewm.categories.dto.CategoryCreateDto;
import ewm.categories.dto.CategoryDto;
import ewm.categories.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid CategoryCreateDto createDto) {
        log.info("Создание категории админом createDto={}", createDto);
        return service.create(createDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId, @RequestBody @Valid CategoryCreateDto createDto) {
        log.info("Изменение категории админом catId={}, createDto={}", catId, createDto);
        return service.update(catId, createDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long catId) {
        log.info("Удаление категории админом catId={}", catId);
        service.deleteById(catId);
    }
}
