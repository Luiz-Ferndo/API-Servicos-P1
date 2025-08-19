package com.prestacaoservicos.entity;

import java.util.Objects;

import com.prestacaoservicos.enums.PhoneTypeEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "user_phone")
public class UserPhone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_phone")
    private Long id;

    @Column(name = "ds_phone", nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_phone", nullable = false, length = 20)
    private PhoneTypeEnum type;

    @ManyToOne
    @JoinColumn(name = "cd_user", nullable = false)
    private User user;

    public UserPhone() {
    }

    public UserPhone(Long id, String phone, PhoneTypeEnum type, User user) {
        this.id = id;
        this.phone = phone;
        this.type = type;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public PhoneTypeEnum getType() {
        return type;
    }

    public void setType(PhoneTypeEnum type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserPhone userPhone = (UserPhone) o;
        return Objects.equals(id, userPhone.id) && Objects.equals(phone, userPhone.phone) && Objects.equals(type, userPhone.type) && Objects.equals(user, userPhone.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone, type, user);
    }

    @Override
    public String toString() {
        return "UserPhone{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", type='" + type + '\'' +
                ", user=" + user +
                '}';
    }
}
