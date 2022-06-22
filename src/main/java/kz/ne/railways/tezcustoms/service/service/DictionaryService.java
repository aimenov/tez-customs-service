package kz.ne.railways.tezcustoms.service.service;

import kz.ne.railways.tezcustoms.service.payload.response.SimpleResponse;

import java.util.List;

public interface DictionaryService {
    List<SimpleResponse> getStationList(String query);

    List<SimpleResponse> getCountryList(String query);

    List<SimpleResponse> getCustomList(String query);

    List<SimpleResponse> getTransitDirectionCodes();
}
