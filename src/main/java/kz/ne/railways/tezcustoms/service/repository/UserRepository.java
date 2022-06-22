package kz.ne.railways.tezcustoms.service.repository;

import kz.ne.railways.tezcustoms.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByIin(String iin);

    User findOneByActivationKey(String activationKey);

    boolean existsByEmailAndEmailActivatedTrue(String email);

}
