package top.leonam.hotbctgamess.interfaces;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Command {
    String name();
    String execute(MessageReceivedEvent event);
}
