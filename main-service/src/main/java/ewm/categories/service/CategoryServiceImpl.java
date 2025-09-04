package ewm.categories.service;

import ewm.categories.Category;
import ewm.categories.CategoryMapper;
import ewm.categories.CategoryRepository;
import ewm.categories.dto.CategoryCreateDto;
import ewm.categories.dto.CategoryDto;
import ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(CategoryCreateDto createDto) {
        Category category = categoryMapper.mapCategoryCreateDtoToModel(createDto);
        return categoryMapper.mapModelToCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(long categoryId, CategoryCreateDto createDto) {
        Category category = checkAndReturnCategory(categoryId);
        category.setName(createDto.getName());
        return categoryMapper.mapModelToCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(long categoryId) {
        checkAndReturnCategory(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).map(categoryMapper::mapModelToCategoryDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(long categoryId) {
        Category category = checkAndReturnCategory(categoryId);
        return categoryMapper.mapModelToCategoryDto(category);
    }

    @Override
    public Category checkAndReturnCategory(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Категория с id= " + id + " не найдена"));
    }
}
