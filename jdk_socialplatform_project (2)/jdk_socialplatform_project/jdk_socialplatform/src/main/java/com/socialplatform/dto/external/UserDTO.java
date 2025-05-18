package com.socialplatform.dto.external;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String username;
    private String email;
    private AddressDTO address;
    private String phone;
    private String website;
    private CompanyDTO company;
    
    @Data
    public static class AddressDTO {
        private String street;
        private String suite;
        private String city;
        private String zipcode;
        private GeoDTO geo;
    }
    
    @Data
    public static class GeoDTO {
        private String lat;
        private String lng;
    }
    
    @Data
    public static class CompanyDTO {
        private String name;
        private String catchPhrase;
        private String bs;
    }
} 