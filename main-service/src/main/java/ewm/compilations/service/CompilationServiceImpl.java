package ewm.compilations.service;

import ewm.compilations.Compilation;
import ewm.compilations.CompilationMapper;
import ewm.compilations.CompilationRepository;
import ewm.compilations.dto.CompilationCreateDto;
import ewm.compilations.dto.CompilationDto;
import ewm.compilations.dto.CompilationUpdateDto;
import ewm.events.Event;
import ewm.events.EventMapper;
import ewm.events.EventRepository;
import ewm.events.dto.EventShortDto;
import ewm.exception.NotFoundException;
import ewm.requests.RequestRepository;
import ewm.requests.dto.ConfirmedRequestsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ewm.requests.enums.RequestStatus.CONFIRMED;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }

        List<Long> compIds = compilations.stream().map(Compilation::getId).toList();
        List<Event> events = eventRepository.findAllByInitiatorIdIn(compIds);

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(eventIds, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequestsDto::getEvent, ConfirmedRequestsDto::getCount));

        List<CompilationDto> result = compilations.stream()
                .map(compilation -> {
                    CompilationDto compilationDto = compilationMapper.mapModelToCompilationDto(compilation);
                    List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                            .map(event -> eventMapper.mapModelToEventShortDto(event, confirmedRequests.get(event.getId())))
                            .collect(Collectors.toList());
                    compilationDto.setEvents(eventShortDtos);
                    return compilationDto;
                })
                .toList();
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compilationId) {
        Compilation compilation = getCompilation(compilationId);
        CompilationDto compilationDto = compilationMapper.mapModelToCompilationDto(compilation);
        if (compilation.getEvents() != null) {
            List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(ConfirmedRequestsDto::getEvent, ConfirmedRequestsDto::getCount));
            compilationDto.setEvents(compilation.getEvents().stream()
                    .map(event -> eventMapper.mapModelToEventShortDto(event, confirmedRequests.get(event.getId())))
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }

    @Override
    public CompilationDto addCompilation(CompilationCreateDto compilationCreateDto) {
        Compilation compilation = compilationMapper.mapCompilationCreateDtoToModel(compilationCreateDto);
        setEvents(compilation, compilationCreateDto.getEvents());
        return setCompilationDto(compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, CompilationUpdateDto compilationUpdateDto) {
        Compilation compilation = getCompilation(compilationId);
        if (compilationUpdateDto.getEvents() != null) {
            Set<Event> events = compilationUpdateDto.getEvents().stream()
                    .map(id -> {
                        Event event = new Event();
                        event.setId(id);
                        return event;
                    }).collect(Collectors.toSet());
            compilation.setEvents(events);
        }
        if (compilationUpdateDto.getPinned() != null) {
            compilation.setPinned(compilationUpdateDto.getPinned());
        }
        String title = compilationUpdateDto.getTitle();
        if (title != null && !title.isBlank()) {
            compilation.setTitle(title);
        }
        return setCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        getCompilation(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    private void setEvents(Compilation compilation, List<Long> eventIds) {
        if (eventIds != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(eventIds));
        }
    }

    private CompilationDto setCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = compilationMapper.mapModelToCompilationDto(compilationRepository.save(compilation));
        if (compilation.getEvents() != null) {
            List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(ConfirmedRequestsDto::getEvent, ConfirmedRequestsDto::getCount));
            compilationDto.setEvents(compilation.getEvents().stream()
                    .map(event -> eventMapper.mapModelToEventShortDto(event, confirmedRequests.get(event.getId())))
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }

    private Compilation getCompilation(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() ->
                new NotFoundException("Подборка с id " + compilationId + " не найдена"));
    }
}
