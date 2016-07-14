package org.nextprot.dao.statements;

import java.util.List;

import org.nextprot.commons.statements.RawStatement;

public interface StatementLoaderService {

	void load(List<RawStatement> statements);

	void deleteAll();

}