package com.service.database;

import com.service.util.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO extends AbstractDAO {
    private static final String GET_USER_BY_LOGIN_PASSWORD = "SELECT username, password FROM users WHERE username = ? AND password = ?";
    private static final String NEW_USER = "INSERT INTO users (username, password) VALUES (?, ?)";
    private static final String GET_USER_BY_LOGIN = "SELECT username, password FROM users WHERE username = ?";

    private RowMapper<User> mapper = (resultSet, i) ->
            new User(resultSet.getString("username"),
                    resultSet.getString("password"));

    public User getUserByLoginPassword(User user) {
        try {
            return getJdbcTemplate().queryForObject(GET_USER_BY_LOGIN_PASSWORD, new Object[]{user.getLogin(), user.getPassword()}, mapper);
        } catch (EmptyResultDataAccessException e) {
            return new User();
        }
    }

    public User newUser(User user) {
        if (isUserExists(user)) {
            return new User();
        }
        getJdbcTemplate().update(NEW_USER, user.getLogin(), user.getPassword());
        getJdbcTemplate().update("INSERT INTO authorities (username) VALUES (?)", user.getLogin());
        return getUserByLoginPassword(user);
    }

    private boolean isUserExists(User user) {
        return getUserByLoginPassword(user).getLogin() != null;
    }

    public User getUserByLogin(User user) {
        try {
            return getJdbcTemplate().queryForObject(GET_USER_BY_LOGIN,
                    new Object[]{user.getLogin()}, mapper);
        } catch (EmptyResultDataAccessException e) {
            return new User();
        }
    }
}
