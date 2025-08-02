package com.prestacaoservicos.entity;

import com.prestacaoservicos.enums.RoleNameEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_role")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nm_role")
    private RoleNameEnum name;

    public Role() {
    }

    private Role(RoleBuilder builder) {
        this.name = builder.name;
    }

    public static RoleBuilder builder() {
        return new RoleBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RoleNameEnum getName() { return name; }
    public void setName(RoleNameEnum name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return java.util.Objects.equals(id, role.id) &&
                name == role.name;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }

    public static class RoleBuilder {
        private RoleNameEnum name;

        public RoleBuilder name(RoleNameEnum name) {
            this.name = name;
            return this;
        }

        public Role build() {
            return new Role(this);
        }
    }
}

