package ewm.categories.service;

import ewm.categories.Category;
import ewm.categories.dto.CategoryCreateDto;
import ewm.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryCreateDto createDto);

    CategoryDto update(long categoryId, CategoryCreateDto createDto);

    void deleteById(long categoryId);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getById(long categoryId);

    Category checkAndReturnCategory(long id);
}
