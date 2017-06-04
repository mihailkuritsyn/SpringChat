package com.mikhail.solsec.chat;

import java.util.List;

public interface ChatRepository {

	List<String> getMessages(int messageIndex);

	void addMessage(String message);

}
