package ewm.requests.service;

import ewm.requests.dto.EventRequestStatusUpdateRequest;
import ewm.requests.dto.EventRequestStatusUpdateResult;
import ewm.requests.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(Long userId,
                                                        Long eventId,
                                                        EventRequestStatusUpdateRequest statusUpdateRequest);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getRequestsByEventOwner(Long userId, Long eventId);

    List<RequestDto> getRequestsByUser(Long userId);
}