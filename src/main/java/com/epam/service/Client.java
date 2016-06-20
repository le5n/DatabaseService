package com.epam.service;

import com.epam.entity.User;
import org.zeromq.ZMQ;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Client {
    public static void main(String[] args) throws Exception {
        try (ZMQ.Context context = ZMQ.context(1)) {
            ZMQ.Socket requester = context.socket(ZMQ.REQ);
            requester.connect("tcp://localhost:5555");

            String command = "getUserByLogin";
            requester.send(command.getBytes(), 0);

            byte[] reply = requester.recv(0);
            User user = (User) getObjectFromBytes(reply);
            System.out.println(user.getLogin());
        }

    }

    private static Object getObjectFromBytes(byte[] reply) throws IOException, ClassNotFoundException {
        Object obj;
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(reply)) {
            try (ObjectInputStream objectIn = new ObjectInputStream(byteIn)) {
                obj = objectIn.readObject();
            }
        }
        return obj;
    }
}
