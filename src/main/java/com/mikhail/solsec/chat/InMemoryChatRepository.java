package com.mikhail.solsec.chat;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryChatRepository implements ChatRepository {

    private final List<String> messages = new CopyOnWriteArrayList<>();
    private final List<String> members = new CopyOnWriteArrayList<>();

    @Override
    public List<String> getMessages(int index) {
        if (messages.isEmpty()) {
            return Collections.<String>emptyList();
        }
        Assert.isTrue((index >= 0) && (index <= messages.size()), "Invalid message index");
        return messages.subList(index, messages.size());
    }

    @Override
    public void addMessage(String message) {
        messages.add(message);
    }

    @Override
    public void addMember(String login) {
        members.add(login);
    }

    @Override
    public List<String> getMembers() {
        return members;
    }

}
