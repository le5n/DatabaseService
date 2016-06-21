package com.epam.database;

import com.epam.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml")
public class UserDAOTest {
    @Autowired
    private UserDAO userDAO;

    @Test
    public void newUser() throws Exception {
        String login = "kek";
        String password = "kek";
        User user = new User(login,password);
        assertEquals(user.getPassword(), userDAO.newUser(login,password).getPassword());
    }

    @Test
    public void getUser() throws Exception {
        String expected = "pek";
        assertEquals(expected, userDAO.getUserByLoginPassword(expected, "pek").getLogin());
    }

    @Test
    public void getUserWithInvalidData() throws Exception {
        assertNull(userDAO.getUserByLoginPassword("invalid", "invalid"));
    }

    @Test
    public void getUserByLogin() throws Exception {
        String login = "pek";
        assertEquals(login, userDAO.getUserByLogin(login).getLogin());
    }

    @Test
    public void getUserByInvalidLogin() throws Exception {
        assertNull(userDAO.getUserByLogin("invalid"));
    }

}