package com.epam.service;

import com.epam.database.UserDAO;
import com.epam.entity.User;
import com.epam.json.JsonObject;
import com.epam.json.JsonObjectFactory;
import com.epam.strategy.Strategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Map;

@Component
public class ServerService {
    @Autowired
    private UserDAO userDAO;

    private Map<String, Strategy> strategyMap = new HashMap<String, Strategy>() {
        private static final long serialVersionUID = -4839350183777912251L;
        {
            put("getUserByLogin", param -> userDAO.getUserByLogin(param[0]));
            put("getUserByLoginPassword",  params -> userDAO.getUserByLoginPassword(params[0], params[1]));
        }
    };

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
                JsonObject object = JsonObjectFactory.getObjectFromJson(request, JsonObject.class);
                Strategy strategy = service.strategyMap.get(object.getCommand());

                User user = strategy.execute(object.getParams());


                String reply = JsonObjectFactory.getJsonString(user);

                responder.send(reply, 0);
            }
        }
    }
}
