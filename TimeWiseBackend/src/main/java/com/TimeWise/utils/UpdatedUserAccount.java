package com.TimeWise.utils;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatedUserAccount {
    private String userEmail;
    private String previousPassword;
    private String newPassword;
    private String shortBioData;
    private String role;
    private String userStatus;
    private String accountVisibility;

}
