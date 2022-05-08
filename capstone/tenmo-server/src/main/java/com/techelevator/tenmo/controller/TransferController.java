package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transfer")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao transferDao;

    public TransferController(TransferDao transferDao){
        this.transferDao = transferDao;
    }



    @PutMapping
    public boolean transferMoney(@RequestBody Transfer transfer){
        return transferDao.transferMoney(transfer);
    }

    @PostMapping
    public boolean logTransfer(@RequestBody Transfer transfer){
        return transferDao.logTransfer(transfer);
}

    @GetMapping("/{username}")
    public List<Transfer> listOfTransfers(@PathVariable String username){
        return transferDao.listOfTransfers(username);
    }

    @GetMapping("/{username}/pending")
    public List<Transfer> listOfPendingTransfers(@PathVariable String username){
        return transferDao.listOfPendingTransfers(username);
    }

    @PutMapping("/{id}")
    public void updateTransfer(@RequestBody Transfer transfer,@PathVariable int id){
        transferDao.updateTransfer(transfer,id);
    }

}
