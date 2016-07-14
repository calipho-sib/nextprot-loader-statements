package org.nextprot.dao.statements;

import org.nextprot.commons.statements.service.StatementLoaderService;
import org.nextprot.commons.statements.service.impl.OracleStatementLoaderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public StatementLoaderService statementLoaderService() {
        return new OracleStatementLoaderServiceImpl("MAPPED_STATEMENTS_FRED");
    }
}
