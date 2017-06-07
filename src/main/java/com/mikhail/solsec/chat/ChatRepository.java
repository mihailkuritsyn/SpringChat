package com.mikhail.solsec.chat;

import java.util.List;

public interface ChatRepository {

    void addMessage(String message);

    List<String> getMessages(int messageIndex);

    void addMember(String login);

    List<String> getMembers();
}
