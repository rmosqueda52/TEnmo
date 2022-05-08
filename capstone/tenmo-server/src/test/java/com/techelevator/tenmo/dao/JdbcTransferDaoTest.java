package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransferDaoTest extends BaseDaoTests {

    private JdbcTransferDao sut;
    private JdbcUserDao sut1;
    private Transfer testTransfer1 = new Transfer();
    private Transfer testTransfer2 = new Transfer();


    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate=new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
        sut1 = new JdbcUserDao(jdbcTemplate);
        testTransfer1.setTransfer_id(3004);
        testTransfer1.setTransfer_type_id(2);
        testTransfer1.setTransfer_status_id(2);
        testTransfer1.setAccount_from(2001);
        testTransfer1.setAccount_to(2002);
        testTransfer1.setAmount(new BigDecimal("20.00"));

        testTransfer2.setTransfer_id(3005);
        testTransfer2.setTransfer_type_id(2);
        testTransfer2.setTransfer_status_id(2);
        testTransfer2.setAccount_from(2002);
        testTransfer2.setAccount_to(2003);
        testTransfer2.setAmount(new BigDecimal("15.00"));
    }

    @Test
    public void transferSendsMoney1() {
        BigDecimal expected = new BigDecimal("2020.00");
        sut.transferMoney(testTransfer1);
        BigDecimal actual = sut1.getBalance(1002);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void transferSendsMoney2() {
        BigDecimal expected = new BigDecimal("1515.00");
        sut.transferMoney(testTransfer2);
        BigDecimal actual = sut1.getBalance(1003);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void transferSubtractsMoneyFromSender1() {
        BigDecimal expected = new BigDecimal("980.00");
        sut.transferMoney(testTransfer1);
        BigDecimal actual = sut1.getBalance(1001);
        Assert.assertEquals(expected, actual);
    }
    @Test
    public void transferSubtractsMoneyFromSender2() {
        BigDecimal expected = new BigDecimal("1985.00");
        sut.transferMoney(testTransfer2);
        BigDecimal actual = sut1.getBalance(1002);
        Assert.assertEquals(expected, actual);
    }
    @Test
    public void logTransferLogsSomething(){
        boolean actual1=sut.logTransfer(testTransfer1);
        Assert.assertTrue(actual1);
        boolean actual2=sut.logTransfer(testTransfer2);
        Assert.assertTrue(actual2);
    }
    @Test public void logTransferLogsTransferWIthExpectedValues(){
        sut.logTransfer(testTransfer1);
        Transfer expected=testTransfer1;
        List<Transfer> transfers=sut.listOfTransfers("user1");
        Transfer actual=transfers.get(2);
        Assert.assertEquals(expected.getAmount(),actual.getAmount());
    }
    @Test public void logTransferLogsTransferWIthExpectedValues2(){
        sut.logTransfer(testTransfer2);
        Transfer expected=testTransfer2;
        List<Transfer> transfers=sut.listOfTransfers("user2");
        Transfer actual=transfers.get(2);
        Assert.assertEquals(expected.getAmount(),actual.getAmount());
    }
    @Test
    public void listOfTransfersReturnsCorrectNumberOfTransfers(){
        List<Transfer>transfers=sut.listOfTransfers("user1");
        int expected=2;
        int actual=transfers.size();
        Assert.assertEquals(expected,actual);
    }
    @Test
    public void listOfTransfersReturnsCorrectNumberOfTransfers2(){
        List<Transfer>transfers=sut.listOfTransfers("user2");
        int expected=2;
        int actual=transfers.size();
        Assert.assertEquals(expected,actual);
    }
    @Test
    public void listOfTransferReturnsTransfersWithCorrectValues(){
        List<Transfer>transfers=sut.listOfTransfers("user1");
        long expected=2002L;
        long actual=transfers.get(0).getAccount_to();
        Assert.assertEquals(expected,actual);
    }
    @Test
    public void listOfTransferReturnsTransfersWithCorrectValues2(){
        List<Transfer>transfers=sut.listOfTransfers("user2");
        long expected=2001L;
        long actual=transfers.get(0).getAccount_from();
        Assert.assertEquals(expected,actual);
    }
}
