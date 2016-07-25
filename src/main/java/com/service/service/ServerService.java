package com.service.service;

import com.service.database.UserDAO;
import com.service.strategy.Strategy;
import com.service.util.entity.User;
import com.service.util.json.JsonObject;
import com.service.util.json.JsonObjectFactory;
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
        private static final long serialVersionUID = -4839350183777912251L;
        {
            put(GET_USER_BY_LOGIN_PASSWORD, user -> userDAO.getUserByLoginPassword(user));
            put(NEW_USER, user -> userDAO.newUser(user));
            put(GET_USER_BY_LOGIN, user -> userDAO.getUserByLogin(user));
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
                    System.out.println(request);
                    Optional<JsonObject> jsonObject = Optional.ofNullable(JsonObjectFactory.getObjectFromJson(request, JsonObject.class));
                    Optional<User> userOptional = jsonObject.map(JsonObject::getUser);
                    Optional<String> commandOptional = jsonObject.map(JsonObject::getCommand);

                    Strategy strategy = service.strategyMap.getOrDefault(commandOptional.orElse(GET_USER_BY_LOGIN_PASSWORD),
                            user -> service.userDAO.getUserByLoginPassword(user));
                    User user = strategy.execute(userOptional.orElseGet(User::new));
                    String reply = JsonObjectFactory.getJsonString(user);

                    System.out.println(reply);
                    responder.send(reply, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
