package hr.fer.zemris.srs.usermgmt.commands;

import hr.fer.zemris.srs.usermgmt.console.ConsoleImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class DeleteCommand implements Command{

    @Override
    public void execute(String username) {
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        try(BufferedReader br = Files.newBufferedReader(ConsoleImpl.dbPath)){
            String line = br.readLine();

            while (!Objects.isNull(line)){
                String storedUsername = line.split(",")[0];
                if(storedUsername.equals(username)){
                    line = br.readLine();
                    found = true;
                    continue;
                }
                sb.append(line).append("\n");
                line = br.readLine();
            }

            Files.writeString(ConsoleImpl.dbPath,sb.toString(), StandardOpenOption.TRUNCATE_EXISTING);

        }catch (IOException e){
            System.out.println("Couldn't read file");
            return;
        }

        if(found) System.out.println("User successfully removed.");
        else System.out.println("User doesn't exist");
    }

}
