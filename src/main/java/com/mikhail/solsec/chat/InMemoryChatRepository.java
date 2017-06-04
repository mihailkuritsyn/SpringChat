package com.mikhail.solsec.chat;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryChatRepository implements ChatRepository {

    private final List<String> messages = new CopyOnWriteArrayList<>();

    public List<String> getMessages(int index) {
        if (messages.isEmpty()) {
            return Collections.<String>emptyList();
        }
        Assert.isTrue((index >= 0) && (index <= messages.size()), "Invalid message index");
        return messages.subList(index, messages.size());
    }

    public void addMessage(String message) {
        messages.add(message);
    }

}
