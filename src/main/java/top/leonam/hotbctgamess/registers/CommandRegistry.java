package top.leonam.hotbctgamess.registers;

import org.springframework.stereotype.Component;
import top.leonam.hotbctgamess.interfaces.Command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandRegistry(List<Command> commandList) {
        for (Command command : commandList) {
            commands.put(command.name(), command);
        }
    }

    public Command get(String name) {
        return commands.get(name);
    }
}

