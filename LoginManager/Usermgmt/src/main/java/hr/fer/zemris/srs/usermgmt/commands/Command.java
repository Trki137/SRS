package hr.fer.zemris.srs.usermgmt.commands;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface Command {
    void execute(String username) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException;
}
