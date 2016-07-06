package com.service.service;

import com.service.entity.User;
import com.service.json.JsonObjectFactory;
import org.zeromq.ZMQ;

public class Client {
    public static void main(String[] args) throws Exception {
        try (ZMQ.Context context = ZMQ.context(1)) {
            ZMQ.Socket requester = context.socket(ZMQ.REQ);
            requester.connect("tcp://localhost:5555");

            String command = "getUserByLogin";
            String login = "kek";

            String jsonString = JsonObjectFactory.getJsonString(command, new User(login));
            requester.send(jsonString.getBytes(), 0);

            String reply = requester.recvStr();
            User user = JsonObjectFactory.getObjectFromJson(reply, User.class);
            System.out.println(user.getLogin());
        }

    }
}
