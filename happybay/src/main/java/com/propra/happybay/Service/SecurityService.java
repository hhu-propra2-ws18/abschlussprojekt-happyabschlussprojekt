package com.propra.happybay.Service;

public interface SecurityService {
    String findLoggedInUsername();
    void autologin(String username, String password);

}
