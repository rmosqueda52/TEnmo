package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    boolean transferMoney(Transfer transfer);

    boolean logTransfer(Transfer transfer);

    List<Transfer> listOfTransfers(String username);

    List<Transfer>listOfPendingTransfers(String username);

    boolean updateTransfer(Transfer transfer, int id);
}
