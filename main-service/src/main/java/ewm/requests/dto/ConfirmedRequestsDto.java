package ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConfirmedRequestsDto {
    private Long count;
    private Long event;
}