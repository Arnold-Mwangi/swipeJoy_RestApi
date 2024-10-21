package blog.posts.p1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null
    private  Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String Description;

    @NotBlank
    @DecimalMin(value="500.00")
    private BigDecimal price;

    @NotBlank
    private LocalDateTime end_date;
    // TODO have a column that will map product with photos
    // TODO have a column to map product with videos

}
