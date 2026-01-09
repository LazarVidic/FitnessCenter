package com.fitnesscenter.dtos;

public class MemberDto {

    private String memberName;
    private String memberSurname;
    private String email;
    private String phone;
    private String username;
    private String password;

   
    private int locationId;

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getMemberSurname() { return memberSurname; }
    public void setMemberSurname(String memberSurname) { this.memberSurname = memberSurname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
}
