package top.werls.nastoys.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;


import java.util.Date;
import lombok.ToString;

@Entity
@Data
public class ApiToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @JsonIgnore
    private String token;

    @Column
    private Long  uid;

    @Column(nullable = false)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUsedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date exceededTime;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
