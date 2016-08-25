package com.service.service;


import com.chat.util.entity.User;
import com.chat.util.json.JsonObjectFactory;
import com.chat.util.json.JsonProtocol;
import com.service.database.UserDAO;
import com.service.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static com.service.strategy.CommandName.*;

@Component
public class ServerService {
    private static final Logger logger = LoggerFactory.getLogger(ServerService.class);
    private UserDAO userDAO;
    // TODO: 7/7/16 implement ssl|https
    private Map<String, Strategy> strategyMap = new TreeMap<String, Strategy>() {
        private static final long serialVersionUID = -163161948782822168L;

        {
            put(GET_USER_BY_LOGIN_PASSWORD, user -> userDAO.getUserByLoginPassword(user));
            put(NEW_USER, user -> userDAO.newUser(user));
            put(GET_USER_BY_LOGIN, user -> userDAO.getUserByLogin(user));
            put("", user -> new User());
        }
    };

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static void main(String[] args) {
        ApplicationContext springContext = new ClassPathXmlApplicationContext("app-context.xml");
        ServerService service = springContext.getBean(ServerService.class);

        try (ZMQ.Context context = ZMQ.context(1)) {
            ZMQ.Socket responder = context.socket(ZMQ.REP);
            responder.bind("tcp://*:11000");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String request = responder.recvStr();
                    Optional<JsonProtocol<User>> protocol = Optional.ofNullable(JsonObjectFactory
                            .getObjectFromJson(request, JsonProtocol.class));
                    Optional<User> userOptional = protocol.map(JsonProtocol::getAttachment);
                    Optional<String> commandOptional = protocol.map(JsonProtocol::getCommand);

                    Strategy strategy = service.strategyMap.getOrDefault(commandOptional.orElse(""), user -> new User());
                    User user = strategy.execute(userOptional.orElseGet(User::new));
                    JsonProtocol<User> jsonProtocol = new JsonProtocol<>("", user);
                    jsonProtocol.setFrom("database");
                    jsonProtocol.setTo("");
                    logger.debug("{}", jsonProtocol);
                    responder.send(jsonProtocol.toString(), 0);
                } catch (Exception e) {
                    logger.error("Error occurred.", e);
                    responder.send(new JsonProtocol().toString());
                }
            }
        }
    }
}
