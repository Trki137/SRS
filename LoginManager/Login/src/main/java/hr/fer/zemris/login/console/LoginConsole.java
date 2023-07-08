package hr.fer.zemris.login.console;

import hr.fer.zemris.login.commands.Command;
import hr.fer.zemris.login.commands.LoginCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginConsole {

    public static final Path dbPath = Path.of("../database.txt");
    private final static String TERMINATE = "exit";

    private final Map<String, Command> commandMap;

    public LoginConsole(){
        this.commandMap = new HashMap<>();

        this.commandMap.put("login", new LoginCommand());
    }

    public static void main(String[] args) {
        LoginConsole loginConsole = new LoginConsole();

        Scanner sc = new Scanner(System.in);

        if(!Files.exists(dbPath)){
            System.out.println("Database doesn't exist.");
            return;
        }

        while(true){
            System.out.print("> ");
            String newLine = sc.nextLine().trim();

            if(newLine.isBlank()){
                System.out.println("Input can't be blank");
                continue;
            }
            if(newLine.equals(TERMINATE)) break;


            String[] split = newLine.split(" ");

            if(!loginConsole.commandMap.containsKey(split[0]) || split.length == 1){
                System.out.println("Command invalid.");
                continue;
            }

            try {
                loginConsole.commandMap.get(split[0]).execute(split[1]);
            }catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
                System.out.println("Couldn't execute command "+ newLine);
            }
        }
    }


}
