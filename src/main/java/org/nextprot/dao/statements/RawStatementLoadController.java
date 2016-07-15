package org.nextprot.dao.statements;

import com.nextprot.api.isoform.mapper.domain.FeatureQueryResult;
import com.nextprot.api.isoform.mapper.domain.impl.FeatureQuerySuccess;
import org.nextprot.commons.statements.RawStatement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.service.StatementLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class RawStatementLoadController {

	@Autowired
	private RawStatementRemoteService rawStatementRemoteService;

	@Autowired
	private IsoformMappingRemoteService isoformMappingRemoteService;

	@Autowired
	private StatementLoaderService statementLoadService;

    @MessageMapping("/rsload")
    @SendTo("/rsload/gene")
    public Message loadGene(Gene gene) throws Exception {

		List<RawStatement> geneStatements;

    	if ("*".equals(gene.getName())) {
			geneStatements = rawStatementRemoteService.getAllGenesRawStatementList();
		}
		else {
			geneStatements = rawStatementRemoteService.getGeneRawStatementList(gene.getName());
		}

		List<RawStatement> loadedStatements = new ArrayList<>(geneStatements.size()*2);
		//List<FeatureQueryFailure> errors = new ArrayList<>();

    	statementLoadService.deleteAll();

		for (RawStatement statement : geneStatements) {

			String annotCat = statement.getValue(StatementField.ANNOTATION_CATEGORY);

			if ("variant".equals(annotCat)) {

				String nextprotAccession = statement.getValue(StatementField.NEXTPROT_ACCESSION);
				String feature = statement.getValue(StatementField.ANNOT_ISO_UNAME);

				boolean propagate = !feature.matches("\\w+-iso\\d-p.+");

				//loadFeatures(statement, feature, annotCat, nextprotAccession, propagate, loadedStatements);
				loadFeaturesAsMap(statement, feature, annotCat, nextprotAccession, propagate, loadedStatements);
			}
			else {
				loadedStatements.add(statement);
			}
		}

    	statementLoadService.load(loadedStatements);

		// oups ca marche plus :(
        return new Message(geneStatements.size() +" statements for gene "+gene.getName()+" -> " + loadedStatements.size()+ " loaded");
    }

	private void loadFeatures(RawStatement statement, String feature, String annotCat, String nextprotAccession, boolean propagate, List<RawStatement> collect) {

		FeatureQueryResult featureQueryResult =
				isoformMappingRemoteService.validateFeature(feature, annotCat, nextprotAccession, propagate);

		if (featureQueryResult.isSuccess()) {
			collect.addAll(toRawStatementList(statement, (FeatureQuerySuccess) featureQueryResult));
		}
		/*else {
			errors.add((FeatureQueryFailure) featureQueryResult);
		}*/
	}

	private void loadFeaturesAsMap(RawStatement statement, String feature, String annotCat, String nextprotAccession, boolean propagate, List<RawStatement> collect) {

		Map<String, Object> map = isoformMappingRemoteService.validateFeatureAsMap(feature, annotCat, nextprotAccession, propagate);

		if ((Boolean)map.get("success"))
			collect.addAll(toRawStatementList(statement, map));
		/*else {
			errors.add((FeatureQueryFailure) featureQueryResult);
		}*/
	}

    private List<RawStatement> toRawStatementList(RawStatement statement, FeatureQuerySuccess result) {

    	List<RawStatement> rawStatementList = new ArrayList<>();

		for (FeatureQuerySuccess.IsoformFeatureResult isoformFeatureResult : result.getData().values()) {

			RawStatement rs = StatementBuilder.createNew().addMap(statement)
					.addField(StatementField.ISOFORM_ACCESSION, isoformFeatureResult.getIsoformName())
					.addField(StatementField.ANNOT_LOC_BEGIN_CANONICAL_REF, String.valueOf(isoformFeatureResult.getFirstIsoSeqPos()))
					.addField(StatementField.ANNOT_LOC_END_CANONICAL_REF, String.valueOf(isoformFeatureResult.getLastIsoSeqPos()))
					.addField(StatementField.ANNOT_ISO_UNAME, String.valueOf(isoformFeatureResult.getIsoSpecificFeature()))
					.build();

			rawStatementList.add(rs);
		}

		return rawStatementList;
	}

	private List<RawStatement> toRawStatementList(RawStatement statement, Map<String, Object> map) {

		List<RawStatement> rawStatementList = new ArrayList<>();

		Map<String, Map<String, Object>> data = (Map<String, Map<String, Object>>) map.get("data");

		for (Map<String, Object> isoformFeatureResult : data.values()) {

			RawStatement rs = StatementBuilder.createNew().addMap(statement)
					.addField(StatementField.ISOFORM_ACCESSION, (String) isoformFeatureResult.get("isoformName"))
					.addField(StatementField.ANNOT_LOC_BEGIN_CANONICAL_REF, String.valueOf(isoformFeatureResult.get("firstIsoSeqPos")))
					.addField(StatementField.ANNOT_LOC_END_CANONICAL_REF, String.valueOf(isoformFeatureResult.get("lastIsoSeqPos")))
					.addField(StatementField.ANNOT_ISO_UNAME, (String) isoformFeatureResult.get("isoSpecificFeature"))
					.build();

			rawStatementList.add(rs);
		}

		return rawStatementList;
	}

	/*@MessageMapping("/rsload")
    @SendTo("/rsload/gene")
	public Message loadAllGenes() throws Exception {

		List<RawStatement> statements = remoteService.getAllGenesRawStatementList();

		statementLoadService.deleteAll();
		statementLoadService.load(statements);

		return new Message(statements.size()+ " raw statements loaded for all genes");
	}*/
}
