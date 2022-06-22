package kz.ne.railways.tezcustoms.service.payload.response;

import lombok.Data;

import java.util.HashMap;

@Data
public class BinResponse {

    private String bin;

    // TODO parse into separate fields;
    private String fio;

    private String organizationName;
    private String kato;
    private String address;

    public BinResponse() {}

    public BinResponse(String organizationName, String kato, String address) {
        this.organizationName = organizationName;
        this.kato = kato;
        this.address = address;
    }

    public BinResponse(HashMap<String, String> map) {
        this.bin = map.get("bin");
        this.fio = map.get("fio");
        this.organizationName = map.get("name");
        this.kato = map.get("katoCode");
        this.address = map.get("katoAddress");
    }


}