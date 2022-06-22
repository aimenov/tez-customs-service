package kz.ne.railways.tezcustoms.service.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleResponse {
    private String id;
    private String code;
    private String name;

    public SimpleResponse(String id, String name){
        this.id = id;
        this.name = name;
    }
}
