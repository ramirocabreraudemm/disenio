package org.example.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="`user`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(name="password_updated_at")
    private Date passwordUpdatedAt;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> students;
    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Date getPasswordChangedAt() {
        return passwordUpdatedAt;
    }

    public void setPasswordChangedAt(Date passwordChangedAt) {
        this.passwordUpdatedAt = passwordChangedAt;
    }
}
