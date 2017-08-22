package com.yjkmust.arcgisdemo.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by GEOFLY on 2017/8/15.
 */
@Entity
public class User {
    @org.greenrobot.greendao.annotation.Id(autoincrement = true)
    private Long Id;
    private String Name;
    private int Phone;
    private String Email;
    private String Address;

    public User(Long id, String address, String email, int phone, String name) {
        Id = id;
        Address = address;
        Email = email;
        Phone = phone;
        Name = name;
    }

    @Generated(hash = 1171757747)
    public User(Long Id, String Name, int Phone, String Email, String Address) {
        this.Id = Id;
        this.Name = Name;
        this.Phone = Phone;
        this.Email = Email;
        this.Address = Address;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getPhone() {
        return Phone;
    }

    public void setPhone(int phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
