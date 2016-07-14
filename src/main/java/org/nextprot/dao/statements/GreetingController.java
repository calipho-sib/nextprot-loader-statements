package org.nextprot.dao.statements;

import java.util.List;

import org.nextprot.commons.statements.RawStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

	@Autowired
	private RawStatementRemoteService remoteService;
	
	@Autowired
	private StatementLoaderService statementLoadService;
	
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting loadGene(HelloMessage geneName) throws Exception {
    		
    	List<RawStatement> statements = remoteService.getGene(geneName.getName());
    	System.err.println(statements.size());

    	statementLoadService.deleteAll();
    	statementLoadService.load(statements);
        //Thread.sleep(3000); // simulated delay
        return new Greeting("Hello, " + geneName.getName() + " " + statements.size());
    }

}
