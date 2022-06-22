package kz.ne.railways.tezcustoms.service.repository;

import kz.ne.railways.tezcustoms.service.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findOneByIdentifier(String identity);

}
