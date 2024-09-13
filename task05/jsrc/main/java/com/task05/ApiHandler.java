package com.task05;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.RetentionSetting;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@LambdaHandler(
    lambdaName = "api_handler",
		layers = {"sdk-layer"},
	roleName = "api_handler-role",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaLayer(
		layerName = "sdk-layer",
		libraries = {"lib/gson-2.10.1.jar"},
		runtime = DeploymentRuntime.JAVA11,
		architectures = {Architecture.ARM64},
		artifactExtension = ArtifactExtension.ZIP
)
public class ApiHandler implements RequestHandler<Map<Object, Object>, APIGatewayV2HTTPResponse> {

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private DynamoDbEnhancedClient dynamoDbClient;
	private DynamoDbTable<DynamoDbItem> table;

	public ApiHandler() {
		dynamoDbClient = DynamoDbEnhancedClient.create();
		table = dynamoDbClient.table("cmtr-326d7d29-Events-test", TableSchema.fromBean(DynamoDbItem.class));
	}

	public APIGatewayV2HTTPResponse handleRequest(Map<Object, Object> event, Context context) {
		String jsonString = gson.toJson(event);
		Request request = gson.fromJson(jsonString, Request.class);
		context.getLogger().log("Request body: "+request);
		DynamoDbItem item = null;
		try {
			 item = new DynamoDbItem(UUID.randomUUID().toString(), request.getPrincipalId(),
					 DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()),
					request.getContent());

			table.putItem(item);
		}catch (Exception e) {
			context.getLogger().log("Unexpected Error: " + e.getMessage());
		}

		return APIGatewayV2HTTPResponse.builder()
				.withStatusCode(201)
				.withBody(gson.toJson(new Response(201, item)))
				.build();

	}
}
