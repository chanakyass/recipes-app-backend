package spring.io.rest.recipes.models.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ingredients", uniqueConstraints = {@UniqueConstraint(name = "uq_name", columnNames = {"name"})})
public class Ingredient {
    @Id
    @SequenceGenerator(name = "ingredient_sequence", sequenceName = "ingredient_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingredient_sequence")
    private Long id;
    private String name;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Ingredient that = (Ingredient) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
