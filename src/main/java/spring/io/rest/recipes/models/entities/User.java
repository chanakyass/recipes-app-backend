package spring.io.rest.recipes.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;
import org.hibernate.Hibernate;
import spring.io.rest.recipes.enums.AuthProvider;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(name = "uq_email", columnNames = {"email"}),
                                            @UniqueConstraint(name = "uq_profile_name", columnNames = "profile_name")})
public class User {
    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    private Long id;
    @Embedded
    private FullName fullName;
    @Column(name = "profile_name")
    private String profileName;
    private String email;
    private String password;
    private LocalDate dob;
    private String userSummary;
    private String imageUrl;
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE},
        fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(name = "fk_user_role__user_id"))},
            inverseJoinColumns = {@JoinColumn(name = "role_id",
            foreignKey = @ForeignKey(name = "fk_user_role__role_id"))},
            uniqueConstraints = {@UniqueConstraint(name = "unique_user_role", columnNames = {"user_id", "role_id"})})
    @ToString.Exclude
    private List<Role> grantedAuthoritiesList;

    @Enumerated
    private AuthProvider authProvider;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate getDob() {
        return dob;
    }

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;

        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFullName(), getEmail(), getPassword(), getProfileName());
    }
}
