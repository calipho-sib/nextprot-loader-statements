package org.nextprot.dao.statements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class IsoformMappingRemoteService {

	private static final String HOST = "http://localhost:8080/nextprot-api-web/";

	private final String validationServiceUrl;
	private final String propagationServiceUrl;

	public IsoformMappingRemoteService() {

		this(HOST);
	}

	public IsoformMappingRemoteService(String host) {

		this.validationServiceUrl = host+"/validate-feature/";
		this.propagationServiceUrl = host+"/propagate-feature/";
	}

	public Map<String, Object> validateFeatureAsMap(String feature, String category, String accession, boolean propagate) {

		String url = (propagate) ? propagationServiceUrl : validationServiceUrl;

		url += category +".json?feature="+feature+"&accession="+accession;

		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.readValue(new URL(url), Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new HashMap<>();
	}

	public FeatureQueryResult validateFeature(String feature, String category, String accession, boolean propagate) {

		String url = (propagate) ? propagationServiceUrl : validationServiceUrl;

		url += category +".json?feature="+feature+"&accession="+accession;

		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.readValue(new URL(url), FeatureQuerySuccess.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
