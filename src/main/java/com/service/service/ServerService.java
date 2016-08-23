package com.service.service;

import com.service.database.UserDAO;
import com.service.strategy.Strategy;
import com.service.util.entity.User;
import com.service.util.json.JsonObjectFactory;
import com.service.util.json.JsonProtocol;
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
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String request = responder.recvStr();
                    Optional<JsonProtocol<User>> jsonObject = Optional.ofNullable(JsonObjectFactory
                            .getObjectFromJson(request, JsonProtocol.class));
                    Optional<User> userOptional = jsonObject.map(JsonProtocol::getAttachment);
                    Optional<String> commandOptional = jsonObject.map(JsonProtocol::getCommand);

                    Strategy strategy = service.strategyMap.getOrDefault(commandOptional.orElse(""), user -> new User());
                    User user = strategy.execute(userOptional.orElseGet(User::new));
                    String reply = JsonObjectFactory.getJsonString(user);
                    responder.send(reply, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
