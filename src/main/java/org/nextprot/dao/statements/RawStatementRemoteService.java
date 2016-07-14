package org.nextprot.dao.statements;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.nextprot.commons.statements.RawStatement;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RawStatementRemoteService {

	private String serviceUrl = "http://kant.isb-sib.ch:9000/gene/";

	public List<RawStatement> getGene(String geneName) {
		String url = serviceUrl + geneName + "/statements";

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
