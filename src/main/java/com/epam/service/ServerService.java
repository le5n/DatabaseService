package com.epam.service;

import com.epam.database.UserDAO;
import com.epam.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
                String command = responder.recvStr();
                if (command.equals("getUserByLogin")) {
                    User user = service.userDAO.getUserByLogin("kek");
                    System.out.println(user);
                    byte[] reply = getSerializableObject(user);

                    responder.send(reply, 0);
                }
            }
        }
    }

    private static byte[] getSerializableObject(Object user) throws IOException {
        byte[] reply;
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream)) {
                objectOutputStream.writeObject(user);
                reply = byteStream.toByteArray();
            }
        }
        return reply;
    }
}
