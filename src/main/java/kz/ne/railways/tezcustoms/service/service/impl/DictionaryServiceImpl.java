package kz.ne.railways.tezcustoms.service.service.impl;

import kz.ne.railways.tezcustoms.service.payload.response.SimpleResponse;
import kz.ne.railways.tezcustoms.service.service.DictionaryService;
import kz.ne.railways.tezcustoms.service.util.LocaleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private final EntityManager entityManager;
    List<String> defaultStations = Arrays.asList("Достык (эксп.)", "Алтынколь", "Хоргос");

    private List<SimpleResponse> transitDirectionCodes;


    @Override
    public List<SimpleResponse> getStationList(String query) {
        query = query.toUpperCase();
        List<Object> result;
        StringBuilder builder = new StringBuilder(" select st_un, sta_no, sta_name from NSI.STA ");
        builder.append(" where ST_END > CURRENT_TIMESTAMP ");

        boolean existQuery = !StringUtils.isEmpty(query);
        if (existQuery) {
            if (NumberUtils.isCreatable(query)) {
                builder.append(" and STA_NO like :v  ");
            } else {
                builder.append(" and upper(STA_NAME) like :v");
            }
        } else {
            builder.append(" and sta_name in (");
            for (int i = 0; i < defaultStations.size(); i++) {
                builder.append("'").append(defaultStations.get(i)).append("'");
                if (i != defaultStations.size() - 1)
                    builder.append(",");
            }
            builder.append(")");
        }

        Query sql = getQuery(query, builder, existQuery);

        sql.setMaxResults(15);
        result = (List<Object>) sql.getResultList();
        List<SimpleResponse> stationList = null;

        if (result != null && !result.isEmpty()) {
            stationList = new ArrayList<>();

            for (Object o : result) {
                Object[] row = (Object[]) o;
                SimpleResponse station = new SimpleResponse();
                station.setId(String.valueOf(row[0]));
                station.setCode(String.valueOf(row[1]));
                station.setName(String.valueOf(row[2]));
                stationList.add(station);
            }
        }

        return stationList;
    }

    @Override
    public List<SimpleResponse> getCountryList(String query) {
        query = query.toUpperCase();
        List<Object> result;
        StringBuilder builder = new StringBuilder(" select country_no, country_name from NSI.COUNTRY ");
        builder.append(" where COU_END > CURRENT_TIMESTAMP ");

        boolean existQuery = !StringUtils.isEmpty(query);
        if (existQuery) {
            if (NumberUtils.isCreatable(query)) {
                builder.append(" and COUNTRY_NO like :v  ");
            } else {
                builder.append(" and upper(COUNTRY_NAME) like :v");
            }
        }

        Query sql = getQuery(query, builder, existQuery);

        result = (List<Object>) sql.getResultList();
        List<SimpleResponse> countryList = null;

        if (result != null && !result.isEmpty()) {
            countryList = new ArrayList<>();

            for (Object o : result) {
                Object[] row = (Object[]) o;
                SimpleResponse station = new SimpleResponse();
                station.setCode(String.valueOf(row[0]));
                station.setName(String.valueOf(row[1]));
                countryList.add(station);
            }
        }

        return countryList;
    }

    @Override
    public List<SimpleResponse> getCustomList(String query) {
        query = query.toUpperCase();
        List<Object> result;
        StringBuilder builder =
                        new StringBuilder(" select custom_code, custom_name, customs_org_un from NSI.ne_customs_orgs ");
        builder.append(" where custom_org_end > CURRENT_TIMESTAMP ");

        boolean existQuery = !StringUtils.isEmpty(query);
        if (existQuery) {
            if (NumberUtils.isCreatable(query)) {
                builder.append(" and custom_code like :v  ");
            } else {
                builder.append(" and upper(custom_name) like :v");
            }
        }

        Query sql = getQuery(query, builder, existQuery);

        result = (List<Object>) sql.getResultList();
        List<SimpleResponse> customList = null;

        if (result != null && !result.isEmpty()) {
            customList = new ArrayList<>();

            for (Object o : result) {
                Object[] row = (Object[]) o;
                SimpleResponse custom = new SimpleResponse();
                custom.setCode(String.valueOf(row[0]));
                custom.setName(String.valueOf(row[1]));
                custom.setId(String.valueOf(row[2]));
                customList.add(custom);
            }
        }

        return customList;
    }

    private Query getQuery(String query, StringBuilder builder, boolean existQuery) {
        Query sql = entityManager.createNativeQuery(builder.toString());

        if (existQuery) {
            if (NumberUtils.isCreatable(query)) {
                sql.setParameter("v", query + "%");
            } else {
                sql.setParameter("v", "%" + query.toUpperCase() + "%");
            }
        }
        return sql;
    }

    public List<SimpleResponse> getTransitDirectionCodes() {
        if (Objects.isNull(transitDirectionCodes)) {
            transitDirectionCodes = List.of(
                    new SimpleResponse("1", LocaleUtils.getDefaultBundle("transit.direction.code1")),
                    new SimpleResponse("2", LocaleUtils.getDefaultBundle("transit.direction.code2")),
                    new SimpleResponse("3", LocaleUtils.getDefaultBundle("transit.direction.code3"))
            );
        }
        return transitDirectionCodes;
    }

//    public DataDao<DicDao> getVagonCountryList(String name) {
//        String sql = "select m.manag_un, c.COUNTRY_NAME from nsi.MANAGEMENT as m join nsi.COUNTRY as c on m.cou_un =c.cou_un where m.manag_end > CURRENT_TIMESTAMP";
//        if(name!=null){
//            sql+=(" and c.COUNTRY_NAME like UCASE('"+name+"%')");
//        }
//        Query q = em.createNativeQuery(sql);
//        List<Object[]> result = q.getResultList();
//        DataDao<DicDao> list = new DataDao<DicDao>();
//        for(Object[] country : result) {
//            list.addRow(new DicDao(((BigInteger)country[0]).longValue(),(String)country[1]));
//        }
//        list.setSuccess(true);
//        return list;
//    }

}
