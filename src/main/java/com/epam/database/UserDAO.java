package com.epam.database;

import com.epam.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO extends AbstractDAO {
    private static final String GET_USER_BY_LOGIN = "SELECT id, login, password FROM user WHERE login = ?";
    private static final String GET_USER_BY_LOGIN_PASSWORD = "SELECT id, login, password FROM user WHERE login = ? AND password = ?";
    private static final String NEW_USER = "INSERT INTO user (login, password) VALUES (?, ?)";

    private RowMapper<User> mapper = (resultSet, i) ->
            new User(resultSet.getInt("id"),
                    resultSet.getString("login"),
                    resultSet.getString("password"));

    public User getUserByLogin(String login) {
        try {
            return getJdbcTemplate().queryForObject(GET_USER_BY_LOGIN, new Object[]{login}, mapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User getUserByLoginPassword(String login, String password) {
        try {
            return getJdbcTemplate().queryForObject(GET_USER_BY_LOGIN_PASSWORD, new Object[]{login, password}, mapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User newUser(String login, String password) {
        getJdbcTemplate().update(NEW_USER, login, password);
        return getUserByLoginPassword(login, password);
    }


}
