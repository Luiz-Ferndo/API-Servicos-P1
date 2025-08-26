package com.prestacaoservicos.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_user")
    private Long id;

    @Column(name = "nm_user", nullable = false, length = 255)
    private String name;

    @Column(name = "ds_email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "ds_password", nullable = false, length = 255)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "cd_user"), inverseJoinColumns = @JoinColumn(name = "cd_role"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserPhone> phones = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "prestador_servicos", joinColumns = @JoinColumn(name = "cd_user"), inverseJoinColumns = @JoinColumn(name = "cd_servico"))
    private Set<Servico> servicosOferecidos = new HashSet<>();

    public User() {
    }

    public User(String name, String email, String password, Set<Role> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public Set<UserPhone> getPhones() { return phones; }
    public void setPhones(Set<UserPhone> phones) { this.phones = phones; }

    public Set<Servico> getServicosOferecidos() { return servicosOferecidos; }
    public void setServicosOferecidos(Set<Servico> servicosOferecidos) { this.servicosOferecidos = servicosOferecidos; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        if (id == null) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}