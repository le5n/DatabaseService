package com.epam.service;

import com.epam.database.UserDAO;
import com.epam.entity.json.*;
import com.epam.entity.relflection.ReflectionAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

@Component
public class ServerService {
    @Autowired
    private UserDAO userDAO;

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
                Object user = ReflectionAPI.getRequestedObjectFromJson(object, service.userDAO);
                String reply = JsonObjectFactory.getJsonString(user);

                responder.send(reply, 0);
            }
        }
    }
}
