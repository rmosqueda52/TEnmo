package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {
    private String url;
    private RestTemplate restTemplate = new RestTemplate();
    private String authToken;

    public void setAuthToken(String token){
        this.authToken = token;
    }

    public TransferService(String url){
        this.url = url;
    }

    public boolean transferMoney(Transfer transfer){
        boolean success=false;
        try{
            restTemplate.put(url+"transfer",makeTransferEntity(transfer));
            success=true;
        }catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    public boolean logTransfer(Transfer transfer){
       boolean success=false;
        try{
            restTemplate.postForObject(url+"transfer",makeTransferEntity(transfer),Boolean.class);
            success=true;
        }catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    public Transfer[] listOfTransfers(String username){
        Transfer[] transfers = null;

        try {
            transfers = restTemplate.exchange(url + "transfer/" + username, HttpMethod.GET,
                    makeAuthEntity(), Transfer[].class ).getBody();
        } catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }

        return transfers;
    }

    public Transfer[] listOfPendingTransfers(String username){
        Transfer[] pendingTransfers=null;

        try {
            pendingTransfers=restTemplate.exchange(url+"transfer/"+username+"/pending",HttpMethod.GET,makeAuthEntity(),Transfer[].class).getBody();
        }catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return pendingTransfers;
    }

    public boolean updateTransfer(Transfer transfer){
        boolean success=false;
        try{
            restTemplate.put(url+"transfer/"+transfer.getTransfer_id(),makeTransferEntity(transfer));
            success=true;
        }catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return success;
    }



    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer,headers);
    }

    private HttpEntity<Void>makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return entity;
    }
}
