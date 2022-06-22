package kz.ne.railways.tezcustoms.service.entity;

import kz.ne.railways.tezcustoms.service.model.ERole;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "roles", schema = "TEZ")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 20)
    private ERole name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users;

    public Role() {

    }

    public Role(ERole name) {
        this.name = name;
    }
}
