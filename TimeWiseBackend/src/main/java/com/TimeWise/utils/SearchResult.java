package com.TimeWise.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
public class SearchResult {

    private String userName;
    private String userEmail;
    private String shortBioData;
    private String role;
    private boolean isFollowing;
    private double matchScore;
}
