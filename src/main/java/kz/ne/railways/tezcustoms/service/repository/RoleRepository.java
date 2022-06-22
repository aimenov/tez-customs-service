package kz.ne.railways.tezcustoms.service.repository;

import kz.ne.railways.tezcustoms.service.entity.Role;
import kz.ne.railways.tezcustoms.service.model.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

    Set<Role> findByNameIn(Collection<ERole> names);

}
