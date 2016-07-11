package com.service.strategy;

import com.service.database.UserDAO;
import com.service.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public class StrategyExecutorTest {
    @Autowired
    private UserDAO userDAO;

    @Test
    public void strategyTestByLoginPassword() throws Exception {
        Strategy strategy = user -> userDAO.getUserByLoginPassword(user);
        String login = "pek";
        String password = "pek";
        User user = strategy.execute(new User(login, password));
        assertEquals(login, user.getLogin());
    }

    @Test
    public void strategyTestByLoginPasswordWithInvalidData() throws Exception {
        Strategy strategy = user -> userDAO.getUserByLoginPassword(user);
        String login = "pek";
        User user = strategy.execute(new User(login, null));
        assertFalse(user.validation());
    }
}