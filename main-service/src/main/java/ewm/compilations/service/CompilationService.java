package ewm.compilations.service;

import ewm.compilations.dto.CompilationCreateDto;
import ewm.compilations.dto.CompilationDto;
import ewm.compilations.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compilationId);

    CompilationDto addCompilation(CompilationCreateDto compilationCreateDto);

    CompilationDto updateCompilation(Long compilationId, CompilationUpdateDto compilationUpdateDto);

    void deleteCompilation(Long compilationId);
}
