package kz.ne.railways.tezcustoms.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "iin"),
        @UniqueConstraint(columnNames = "email")}, schema = "TEZ")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

//    @NotBlank
    @Column(name = "iin")
    @Size(max = 12)
    private String iin;

    @NotBlank
    @Size(max = 50)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="company_id", nullable=false)
    private Company company;

    @Column(name = "first_name")
    @Size(max = 50)
    private String firstName;

    @Column(name = "last_name")
    @Size(max = 50)
    private String lastName;

    @Column(name = "middle_name")
    @Size(max = 50)
    private String middleName;

    @Column(name = "created_date")
    @CreatedDate
    private Date createdDate;

    @Column(name = "last_modified_date")
    @LastModifiedDate
    private Date lastModifiedDate;

    @Column(name = "expeditor_code")
    @Size(max = 10)
    private String expeditorCode;

    /**
     * Учетка активирована
     */
    @Column(name = "activated", nullable = false)
    private boolean activated = false;

    /**
     * E-mail активирован
     */
    @Column(name = "email_activated", nullable = false)
    private boolean emailActivated = false;

    /**
     * Код активации
     */
    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    /**
     * Срок кода активации
     */
    @Column(name = "activation_key_date")
    private Timestamp activationKeyDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", schema = "TEZ",
                    joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
                    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "register_file_path")
    private String registerFilePath;

    public User() {}

    public User(String email, String password, String phone) {
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}

