package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class JdbcUserDaoTest extends BaseDaoTests{

//    private static final User user1=new User(1001L,"user1","user1",true)
private JdbcUserDao sut;

@Before
    public void setup(){
    JdbcTemplate jdbcTemplate=new JdbcTemplate();
    jdbcTemplate.setDataSource(dataSource);
    sut=new JdbcUserDao(jdbcTemplate);
}

@Test
    public void getBalanceReturnsCorrectBalance(){
    BigDecimal actual1=sut.getBalance(1001);
    BigDecimal expected1= new BigDecimal("1000.00");
    Assert.assertEquals(expected1,actual1);

    BigDecimal actual2=sut.getBalance(1002);
    BigDecimal expected2=new BigDecimal("2000.00");
    Assert.assertEquals(expected2,actual2);
}

@Test
    public void getAccountIdByUserIdReturnsCorrectValue(){
    long actual1=sut.getAccountIdByUserId(1001);
    long expected1=2001L;
    Assert.assertEquals(expected1,actual1);

    long actual2=sut.getAccountIdByUserId(1002);
    long expected2=2002L;
    Assert.assertEquals(expected2,actual2);
}


}
