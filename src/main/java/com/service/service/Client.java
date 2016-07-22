package com.service.service;

import com.service.util.entity.User;
import com.service.util.json.JsonObjectFactory;
import org.zeromq.ZMQ;

public class Client {
    public static void main(String[] args) throws Exception {
        try (ZMQ.Context context = ZMQ.context(1)) {
            ZMQ.Socket requester = context.socket(ZMQ.REQ);
            requester.connect("tcp://localhost:11000");

            String command = "getUserByLoginPassword";
            String login = "kek";
            String password = "kek";

            String jsonString = JsonObjectFactory.getJsonString(command, new User(login, password));
            requester.send(jsonString.getBytes(), 0);

            String reply = requester.recvStr();
            User user = JsonObjectFactory.getObjectFromJson(reply, User.class);
            if (user != null) {
                System.out.println(user.getLogin());
            } else {
                System.out.println("User is null");
            }
        }

    }
}
