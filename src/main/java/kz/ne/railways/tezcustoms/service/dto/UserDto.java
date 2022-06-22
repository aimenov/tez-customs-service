package kz.ne.railways.tezcustoms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String email;
    private String companyName;
    private String companyDirector;
    private String phone;
    private String address;
    private String iinBin;
    private String firstName;
    private String lastName;
}
