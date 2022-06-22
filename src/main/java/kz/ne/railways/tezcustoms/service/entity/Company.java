package kz.ne.railways.tezcustoms.service.entity;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Data
@Table(name = "company", uniqueConstraints = {@UniqueConstraint(columnNames = "identifier")}, schema = "tez")
public class Company extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "iin")
    @Size(max = 12)
    private String iin;

    @Column(name = "bin")
    @Size(max = 12)
    private String bin;

    @Column(name = "identifier", nullable = false)
    @Size(max = 12)
    private String identifier;

    @Size(max = 250)
    private String address;

    @Column(name = "name")
    @Size(max = 1000)
    private String name;

    @Column(name = "director_name")
    @Size(max = 120)
    private String directorName;

    @Column(columnDefinition = "boolean default false", name = "is_company")
    private boolean isCompany;

    @Size(max = 10)
    private String kato;

    public Company() {}

    public Company(String identifier, String address, String name, String directorName, boolean isCompany, String kato) {
        this.identifier = identifier;
        this.address = address;
        this.name = name;
        this.directorName = directorName;
        this.isCompany = isCompany;
        this.kato = kato;
    }

}
