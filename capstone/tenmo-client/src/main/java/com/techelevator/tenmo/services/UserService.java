package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private String url;
    private RestTemplate restTemplate = new RestTemplate();
    private String authToken;

    public void setAuthToken(String token){
        this.authToken = token;
    }

    public UserService(String url){
        this.url = url;
    }

    public BigDecimal getBalance(Long userId){

            return restTemplate.exchange(url + "user/" + userId+"/balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();

    }
   public User[]displayAllUsers(){
        User[]users=null;
        try{
            users=restTemplate.exchange(url+"user",HttpMethod.GET,makeAuthEntity(),User[].class).getBody();
        }catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return users;
   }

   public Long getAccountIdByUserId(int userId){
        long account_id= 0;
        try{
            account_id=restTemplate.exchange(url+"user/"+userId+"/account",HttpMethod.GET,makeAuthEntity(),Long.class).getBody();
        }catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return account_id;
   }


private HttpEntity<Void>makeAuthEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(authToken);
    HttpEntity<Void> entity = new HttpEntity<>(headers);
    return entity;
}
}
