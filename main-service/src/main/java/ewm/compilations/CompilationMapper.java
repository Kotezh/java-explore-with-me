package ewm.compilations;

import ewm.compilations.dto.CompilationCreateDto;
import ewm.compilations.dto.CompilationDto;
import org.springframework.stereotype.Component;

@Component
public class CompilationMapper {
    public Compilation mapCompilationCreateDtoToModel(CompilationCreateDto dto) {
        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.isPinned())
                .build();
    }

    public CompilationDto mapModelToCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .build();
    }
}
