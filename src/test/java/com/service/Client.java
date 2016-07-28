package com.service;

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

            String good = JsonObjectFactory.getJsonString(command, new User(login, password));
            String[] jsonString = {good, "{\"command\": \"Poop\", \"user\":{\"id\":0,\"login\":\"null\",\"password\":\"null\"}}",
                    "{\"command\": \"Poop\", \"user\":{\"id\":0,\"login\":null,\"password\":null}}",
                    "{\"command\": \"Poop\", \"user\":{\"id\":0,\"login\":,\"password\":}}",
                    "{\"command\": \"Poop\", \"user\":{}}",
                    "{\"command\": \"Poop\", \"user\"}",
                    "{\"command\": \"Poop\"}",
                    "{\"command\": \"getUserByLoginPassword\"}",
                    "{}"};
            for (String s : jsonString) {
                requester.send(s.getBytes(), 0);

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
}
