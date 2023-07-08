package hr.fer.zemris.srs.pm;

import hr.fer.zemris.srs.commands.Command;
import hr.fer.zemris.srs.commands.GetCommand;
import hr.fer.zemris.srs.commands.InitCommand;
import hr.fer.zemris.srs.commands.PutCommand;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PasswordManagerImpl implements PasswordManager{
    private String currentCommandLine;

    private final Path path;

    private final Map<String,Command> commandMap;

    public static final String EMPTY_BASE_PASSWORD = "EMPTY_PASSWORD";

    public PasswordManagerImpl(){
        this.path = Path.of("./passwordManager.txt");
        this.commandMap = new HashMap<>();

        commandMap.put("init", new InitCommand());
        commandMap.put("put", new PutCommand());
        commandMap.put("get", new GetCommand());
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        PasswordManagerImpl passwordManager = new PasswordManagerImpl();
        passwordManager.commandMap.get("put");

        while(true){
            System.out.print("> ");
            passwordManager.currentCommandLine = sc.nextLine();
            String command = passwordManager.currentCommandLine.split(" ")[0];

            if(!passwordManager.commandMap.containsKey(command)){
                System.out.printf("Command %s is not valid%n", command);
                continue;
            }
            passwordManager.execute(passwordManager.commandMap.get(command));
        }
    }

    @Override
    public void execute(Command command) {
        command.run(currentCommandLine,path);
    }
}
