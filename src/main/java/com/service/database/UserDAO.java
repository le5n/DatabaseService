package com.service.database;

import com.service.util.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO extends AbstractDAO {
    private static final String GET_USER_BY_LOGIN_PASSWORD = "SELECT id, login, password FROM user WHERE login = ? AND password = ?";
    private static final String NEW_USER = "INSERT INTO user (login, password) VALUES (?, ?)";
    private static final String GET_USER_BY_LOGIN = "SELECT id, login, password FROM user WHERE login = ?";

    private RowMapper<User> mapper = (resultSet, i) ->
            new User(resultSet.getInt("id"),
                    resultSet.getString("login"),
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
