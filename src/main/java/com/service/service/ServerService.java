package com.service.service;

import com.service.database.UserDAO;
import com.service.entity.User;
import com.service.json.*;
import com.service.strategy.Strategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

import java.util.Map;
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
        }
    };

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext springContext = new ClassPathXmlApplicationContext("app-context.xml");
        ServerService service = springContext.getBean(ServerService.class);

        try (ZMQ.Context context = ZMQ.context(1)) {
            ZMQ.Socket responder = context.socket(ZMQ.REP);
            responder.bind("tcp://*:5555");
            while (!Thread.currentThread().isInterrupted()) {
                String request = responder.recvStr();
                JsonObject jsonObject = JsonObjectFactory.getObjectFromJson(request, JsonObject.class);
                Strategy strategy = service.strategyMap.get(jsonObject.getCommand());

                User user = strategy.execute(jsonObject.getUser());
                String reply = JsonObjectFactory.getJsonString(user);
                responder.send(reply, 0);
            }
        }
    }
}
