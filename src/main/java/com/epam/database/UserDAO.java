package com.epam.database;

import com.epam.entity.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO extends AbstractDAO {
    private static final String GET_USER_BY_LOGIN = "SELECT id, login, password FROM user WHERE login = ?";

    private RowMapper<User> mapper = (resultSet, i) ->
            new User(resultSet.getInt("id"),
                    resultSet.getString("login"),
                    resultSet.getString("password"));

    public User getUserByLogin(String login) {
        return getJdbcTemplate().queryForObject(GET_USER_BY_LOGIN, new Object[]{login}, mapper);
    }
}
