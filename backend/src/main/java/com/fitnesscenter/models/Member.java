package com.fitnesscenter.models;

import jakarta.persistence.*;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private int memberId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "member_surname", nullable = false)
    private String memberSurname;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "roll", nullable = false)
    private Roll roll;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "username", nullable = false)
    private String username;

    // ✅ član/zaposleni pripada lokaciji (seller/admin obavezno, user može null ako još nemaš logiku)
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = true)
    private Location location;

    public Member() {}

    public Member(String memberName, String memberSurname, String phone, Roll roll,
                  String email, String password, String username, Location location) {
        this.memberName = memberName;
        this.memberSurname = memberSurname;
        this.phone = phone;
        this.roll = roll;
        this.email = email;
        this.password = password;
        this.username = username;
        this.location = location;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberSurname() {
        return memberSurname;
    }

    public void setMemberSurname(String memberSurname) {
        this.memberSurname = memberSurname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Roll getRoll() {
        return roll;
    }

    public void setRoll(Roll roll) {
        this.roll = roll;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
