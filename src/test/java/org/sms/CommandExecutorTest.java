package org.sms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.tcontrol.sms.commands.CommandExecutor;
import org.tcontrol.sms.commands.CommandResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.tcontrol.sms.commands.STATUS.FAILURE;
import static org.tcontrol.sms.commands.STATUS.OK;

@ContextConfiguration(classes = CommandConfiguration.class, loader = AnnotationConfigContextLoader.class)
@SpringBootTest
public class CommandExecutorTest {

    @Autowired
    private CommandExecutor commandExecutor;

    @Test
    public void success() {
        CommandResult res = commandExecutor.run("on", "10");
        assertNotNull(res);
        assertEquals(OK, res.getStatus());
    }

    @Test
    public void failed() {
        CommandResult res = commandExecutor.run("ABC", "10");
        assertNotNull(res);
        assertEquals(FAILURE, res.getStatus());
        assertTrue(res.getMessage().contains("unknown command: ABC"));
    }
}
