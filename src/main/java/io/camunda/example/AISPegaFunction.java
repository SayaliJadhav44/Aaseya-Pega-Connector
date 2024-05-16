package io.camunda.example;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import io.camunda.example.dto.AISPegaRequest;
import io.camunda.example.dto.AISPegaResult;

@OutboundConnector(name = "Aaseya Pega Connector", inputVariables = { "url", "method", "payload", "username",
		"password" }, type = "io.camunda:pega-inspection:1")
@ElementTemplate(id = "io.camunda.connector.Template.v1", name = "Template connector", version = 1, description = "Aaseya inspection solutions Pega Connector", icon = "icon.svg", documentationRef = "https://docs.camunda.io/docs/components/connectors/out-of-the-box-connectors/available-connectors-overview/", propertyGroups = {
		@ElementTemplate.PropertyGroup(id = "authentication", label = "Authentication"),
		@ElementTemplate.PropertyGroup(id = "input", label = "Input") }, inputDataClass = AISPegaRequest.class)
public class AISPegaFunction implements OutboundConnectorFunction {

	private static final Logger LOGGER = LoggerFactory.getLogger(AISPegaFunction.class);

	@Override
	public Object execute(OutboundConnectorContext context) throws IOException {
		final var connectorRequest = context.bindVariables(AISPegaRequest.class);
		return executeConnector(connectorRequest);
	}

	private AISPegaResult executeConnector(final AISPegaRequest connectorRequest) throws IOException {
		// TODO: implement connector logic
		LOGGER.info("Executing my connector with request {}", connectorRequest);
		String urlStr = connectorRequest.url();
		String username = connectorRequest.username();
		String password = connectorRequest.password();
		Object payload = connectorRequest.payload();

		System.out.println(urlStr);
		System.out.println(username);
		System.out.println(password);
		System.out.println(payload.toString());

//		if (placeID != null && placeID.toLowerCase().startsWith("fail")) {
//			throw new ConnectorException("FAIL", "My property started with 'fail', was: " + placeID);
//		}		

		String urlString = urlStr;
		URL url = new URL(urlString);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setDoOutput(true);
		http.setDoInput(true);
		http.setRequestProperty("Accept", "application/json");
		http.setRequestMethod("POST");
		http.setRequestProperty("Content-Type", "application/json");

//		payload = "{\"caseTypeID\":\"Aaseya-AIF-Work-Inspection\",\"content\":{\"SearchID\":\"LCSMunitionsCentre\",\"InspectionType\":\"AmmunitionSafetyCheck\",\"InspectionReason\":\"FilledComplaint\",\"InspectionReasonDesc\":\"Test\",\"Source\":\"ReInspection\",\"pxCreateOperator\":\"API\",\"pxCreateOpName\":\"API\",\"InsCategoryID\":\"ICAIF-1906\",\"AssignedInspector\":\"Inspector@AIF\",\"InspectorName\":\"Inspector\",\"EffortRequired\":5,\"StartDate\":\"20240409\"},\"pageInstructions\":[{\"instruction\":\"UPDATE\",\"target\":\"EntityInfo\",\"content\":{\"Addreess\":\"Liverpool,UK\",\"Category\":\"ArtilleryUnitLogistic\",\"CoordinatesXY\":\"53.4083714,-2.9915726\",\"EmailAddress\":\"samsmith@mod.uk.com\",\"EntityName\":\"LCSMunitionsCentre\",\"EntitySize\":\"Medium\",\"EntityType\":\"Site\",\"InspectionType\":\"AmmunitionSafetyCheck\",\"IsAddressAvailabe\":\"true\",\"OwnerName\":\"LogisticCommodityServices\",\"PhoneNumber\":\"445423176745\",\"Segment\":\"Army\",\"ZoneName\":\"England\"}}]}";
		payload = new ObjectMapper().writeValueAsString(payload);
		String userpass = username + ":" + password;
		System.out.println("https://www.javatpoint.com");
		String basicAuth = "Basic : " + new String(Base64.getEncoder().encode(userpass.getBytes()));
		System.out.println(basicAuth);
		http.setRequestProperty("Authorization", basicAuth);

		try (DataOutputStream os = new DataOutputStream(http.getOutputStream())) {
			os.writeBytes(payload.toString());
			os.flush();
		}

		http.connect();

		String pegaResponse;
		if (http.getResponseCode() == 201) {
			pegaResponse = new String(http.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			LOGGER.info("Pega Response : " + pegaResponse);
		} else {
			LOGGER.error("Error accessing Pega API: " + http.getResponseCode() + " - " + http.getResponseMessage());
			// Throwing an exception will fail the job

			throw new IOException();
		}

		return new AISPegaResult(pegaResponse);
	}
}
