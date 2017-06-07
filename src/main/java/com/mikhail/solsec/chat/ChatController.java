package com.mikhail.solsec.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/mvc/chat")
public class ChatController {

    private final ChatRepository chatRepository;

    private final Map<DeferredResult<List<String>>, Integer> chatRequests = new ConcurrentHashMap<>();

    @Autowired
    public ChatController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<List<String>> getMessages(@RequestParam int messageIndex) {

        final DeferredResult<List<String>> deferredResult = new DeferredResult<>(null, Collections.emptyList());
        chatRequests.put(deferredResult, messageIndex);

        deferredResult.onCompletion(() -> chatRequests.remove(deferredResult));

        List<String> messages = chatRepository.getMessages(messageIndex);
        if (!messages.isEmpty()) {
            deferredResult.setResult(messages);
        }

        return deferredResult;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void postMessage(@RequestParam String message) {
        chatRepository.addMessage(message);
        for (Entry<DeferredResult<List<String>>, Integer> entry : chatRequests.entrySet()) {
            List<String> messages = chatRepository.getMessages(entry.getValue());
            entry.getKey().setResult(messages);
        }
    }

    @RequestMapping(value = "/addMember", method = RequestMethod.POST)
    @ResponseBody
    public void addMember(@RequestParam String login) {
        System.out.println("!!!!!!" + login);

        chatRepository.addMember(login);
    }

    @RequestMapping(value = "/getMembers", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getMembers() {
        System.out.println("!!!getMembers");

        List<String> members = chatRepository.getMembers();
        System.out.println("!!1members" + members);
        return members;
    }

}
