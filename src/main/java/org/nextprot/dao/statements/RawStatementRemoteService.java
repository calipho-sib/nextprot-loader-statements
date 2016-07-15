package org.nextprot.dao.statements;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nextprot.commons.statements.RawStatement;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
public class RawStatementRemoteService {

	private String serviceUrl = "http://kant.isb-sib.ch:9000";

	public RawStatementRemoteService(String datasource) {
		serviceUrl = "http://kant.isb-sib.ch:9000/"+datasource;
	}

	// BioEditor Raw Statement service for a Gene. Example for msh2: http://kant.isb-sib.ch:9000/bioeditor/gene/msh2/statements
	public List<RawStatement> getGeneRawStatementList(String geneName) {

		return deserialize(serviceUrl + "/gene/"+ geneName + "/statements");
	}

	// BioEditor Raw Statement service for all data (CAREFUL WITH THIS ONE) http://kant.isb-sib.ch:9000/bioeditor/statements
	public List<RawStatement> getAllGenesRawStatementList() {

		return deserialize(serviceUrl + "/statements");
	}

	private List<RawStatement> deserialize(String url) {

		ObjectMapper mapper = new ObjectMapper();

		List<RawStatement> obj = null;
		try {
			obj = mapper.readValue(new URL(url), new TypeReference<List<RawStatement>>() {
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return obj;
	}
}
