package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/user")
public class UserController {

    private UserDao userDao;

    public UserController(UserDao userDao){
        this.userDao = userDao;
    }

    @GetMapping("/{id}/balance")
    public BigDecimal getBalance(@PathVariable int id){
        return userDao.getBalance(id);
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userDao.findAll();
}

    @GetMapping("/{id}/account")
    public Long getAccountIdByUserId(@PathVariable int id){
        return userDao.getAccountIdByUserId(id);
}









}
