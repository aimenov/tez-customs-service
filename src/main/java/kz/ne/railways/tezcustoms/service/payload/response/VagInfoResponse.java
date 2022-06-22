package kz.ne.railways.tezcustoms.service.payload.response;


import lombok.Data;

import java.util.List;

@Data
public class VagInfoResponse {
    private List<Object[]> lists;
}
