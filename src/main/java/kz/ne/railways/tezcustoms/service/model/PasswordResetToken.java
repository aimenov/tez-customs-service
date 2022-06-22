package kz.ne.railways.tezcustoms.service.model;

import kz.ne.railways.tezcustoms.service.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
public class PasswordResetToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expirationDate;
}
