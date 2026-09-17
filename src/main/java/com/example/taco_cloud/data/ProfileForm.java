package com.example.taco_cloud.data;


import lombok.Data;

@Data
public class ProfileForm {
    private String fullname;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String phoneNumber;
}
