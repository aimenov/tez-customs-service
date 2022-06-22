package kz.ne.railways.tezcustoms.service.mapper;

import kz.ne.railways.tezcustoms.service.entity.TnVed;

public class TnVedMapper {

    public static TnVed fromId(Long id) {
        TnVed tnVed = new TnVed();
        tnVed.setId(id);
        return tnVed;
    }
}
