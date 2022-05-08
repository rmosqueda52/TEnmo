package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public boolean transferMoney(Transfer transfer) {
        String sqlWithdraw = "UPDATE account SET balance = balance - ? " +
                    " WHERE account_id = ?";

        String sqlDeposit = "UPDATE account SET balance = balance + ? " +
                " WHERE account_id = ?";



        return jdbcTemplate.update(sqlDeposit, transfer.getAmount(),transfer.getAccount_to()) == 1
                &&  jdbcTemplate.update(sqlWithdraw, transfer.getAmount(), transfer.getAccount_from()) == 1;

    }

    @Override
    public boolean logTransfer(Transfer transfer) {
        String createTransfer = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                " VALUES(?, ?, ?, ?, ?)";
        return jdbcTemplate.update(createTransfer, transfer.getTransfer_type_id(), transfer.getTransfer_status_id(),
                transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount()) ==1;
    }

    @Override
    public List<Transfer> listOfTransfers(String username) {
        List<Transfer> userListOfTransfers = new ArrayList<>();

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount, C.username AS username_from, E.username AS username_to\n" +
                "FROM transfer A\n" +
                "JOIN account B ON A.account_from = B.account_id\n" +
                "JOIN account D ON A.account_to = D.account_id\n" +
                "JOIN tenmo_user C ON B.user_id = C.user_id\n" +
                "JOIN tenmo_user E ON D.user_id = E.user_id\n" +
                "WHERE C.username = ? OR E.username = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username,username);

        while(results.next()){
            userListOfTransfers.add(mapToRowTransfer(results));
        }

        return userListOfTransfers;
    }

    @Override
    public List<Transfer> listOfPendingTransfers(String username) {
        List<Transfer>transferList=new ArrayList<>();
        String sql="SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount, C.username AS username_from, E.username AS username_to\n" +
                "FROM transfer A\n" +
                "JOIN account B ON A.account_from = B.account_id\n" +
                "JOIN account D ON A.account_to = D.account_id\n" +
                "JOIN tenmo_user C ON B.user_id = C.user_id\n" +
                "JOIN tenmo_user E ON D.user_id = E.user_id\n" +
                "WHERE C.username = ? AND transfer_status_id=1;";
        SqlRowSet results=jdbcTemplate.queryForRowSet(sql,username);

        while (results.next()){
            transferList.add(mapToRowTransfer(results));
        }
        return transferList;
    }

    @Override
    public boolean updateTransfer(Transfer transfer, int id) {
String sql="UPDATE transfer SET transfer_status_id=? WHERE transfer_id=?";
return jdbcTemplate.update(sql,transfer.getTransfer_status_id(),transfer.getTransfer_id())==1;
    }


    private Transfer mapToRowTransfer(SqlRowSet sqlRowSet){
        Transfer transfer = new Transfer();
        transfer.setTransfer_id(sqlRowSet.getLong("transfer_id"));
        transfer.setTransfer_type_id(sqlRowSet.getLong("transfer_type_id"));
        transfer.setTransfer_status_id(sqlRowSet.getLong("transfer_status_id"));
        transfer.setAccount_from(sqlRowSet.getLong("account_from"));
        transfer.setAccount_to(sqlRowSet.getLong("account_to"));
        transfer.setAmount(sqlRowSet.getBigDecimal("amount"));
        transfer.setUsername_from(sqlRowSet.getString("username_from"));
        transfer.setUsername_to(sqlRowSet.getString("username_to"));
        return transfer;
    }

}
