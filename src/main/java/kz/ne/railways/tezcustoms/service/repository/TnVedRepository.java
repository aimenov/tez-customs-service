package kz.ne.railways.tezcustoms.service.repository;

import kz.ne.railways.tezcustoms.service.entity.TnVed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TnVedRepository extends JpaRepository<TnVed, Long> {

    @Query(value = "WITH RECURSIVE tnv_tree AS" +
            "    (select id, text, parent_id, level" +
            "       from tez.tn_ved" +
            "      where tn_ved.code = :code" +
            "     UNION ALL" +
            "     select tv.id, tv.text, tv.parent_id, tv.level" +
            "       from tez.tn_ved tv" +
            "       JOIN tnv_tree t ON t.parent_id = tv.id)" +
            "    select text from tnv_tree order by level ", nativeQuery = true)
    List<String> findTextListByCode(@Param("code") String code);

}
