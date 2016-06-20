package com.epam.service;

import com.epam.database.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;

@Component
public class ServerService {
    @Autowired
    private UserDAO userDAO;

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static void main(String[] args) {
        ApplicationContext springContext = new ClassPathXmlApplicationContext("app-context.xml");
        ServerService service = springContext.getBean(ServerService.class);

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REP);
        socket.bind("tcp://*:5555");

        while (!Thread.currentThread().isInterrupted()) {
            ZFrame identity = ZFrame.recvFrame(socket);
            String command = socket.recvStr();
            if (command.equals("getUserByLogin")) {
                service.userDAO.getUserByLogin("kek");
            }
        }
    }
}
