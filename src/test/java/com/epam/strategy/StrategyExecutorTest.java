package com.epam.strategy;

import com.epam.database.UserDAO;
import com.epam.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public class StrategyExecutorTest {
    @Autowired
    private UserDAO userDAO;

    @Test
    public void strategyTestByLogin() throws Exception {
        Strategy strategy = params -> userDAO.getUserByLogin(params[0]);
        String login = "pek";
        User user = strategy.execute(login);
        assertEquals(login, user.getLogin());
    }

    @Test
    public void strategyWithInvalidData() throws Exception {
        Strategy strategy = params ->  userDAO.getUserByLogin(params[0]);
        User user = strategy.execute(null, null);
        assertNull(user);
    }

    @Test
    public void strategyTestByLoginPassword() throws Exception {
        Strategy strategy = params -> userDAO.getUserByLoginPassword(params[0], params[1]);
        String expected = "pek";
        User user = strategy.execute(expected, "pek");
        assertEquals(expected, user.getLogin());
    }

    @Test
    public void strategyTestByLoginPasswordWithInvalidData() throws Exception {
        Strategy strategy = params -> userDAO.getUserByLoginPassword(params[0], params[1]);
        String expected = "pek";
        User user = strategy.execute(expected, null);
        assertNull(user);
    }
}