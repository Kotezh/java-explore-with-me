package ewm.categories;

import ewm.categories.dto.CategoryCreateDto;
import ewm.categories.dto.CategoryDto;
import org.springframework.stereotype.Component;

@Component

public class CategoryMapper {
    public Category mapCategoryCreateDtoToModel(CategoryCreateDto dto) {
        return Category.builder().name(dto.getName()).build();
    }

    public CategoryDto mapModelToCategoryDto(Category category) {
        return CategoryDto.builder().id(category.getId()).name(category.getName()).build();
    }
}
