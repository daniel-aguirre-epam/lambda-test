package com.task06;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.RetentionSetting;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutRequest;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
    lambdaName = "audit_producer",
		layers = {"sdk-layer"},
	roleName = "audit_producer-role",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@DynamoDbTriggerEventSource(
		targetTable = "Configuration",
		batchSize = 10
)
@LambdaLayer(
		layerName = "sdk-layer",
		libraries = {"lib/gson-2.10.1.jar"},
		runtime = DeploymentRuntime.JAVA11,
		architectures = {Architecture.ARM64},
		artifactExtension = ArtifactExtension.ZIP
)
public class AuditProducer implements RequestHandler<Map<Object, Object>, Void> {

	private DynamoDbEnhancedClient dynamoDbenhancedClient;
	private DynamoDbClient dynamoDbClient;
	private DynamoDbTable<DynamoDbItem> table;



	public AuditProducer() {
		dynamoDbClient = DynamoDbClient.create();
		dynamoDbenhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
		table = dynamoDbenhancedClient.table("cmtr-326d7d29-Audit-test", TableSchema.fromBean(DynamoDbItem.class));
	}

	public Void handleRequest(Map<Object, Object> event, Context context) {
		List<Map<String, Object>> records = (List<Map<String, Object>>) event.get("Records");

		records.stream().forEach(record -> {
			String eventName = (String)record.get("eventName");
			Map<String, Object> dynamodb = (Map<String, Object>) record.get("dynamodb");
			Map<String, Object> newImage = (Map<String, Object>) dynamodb.get("NewImage");

			Map<String, Object> keyMap = (Map<String, Object>) newImage.get("key");
			String key = (String) keyMap.values().stream().findFirst().orElse("");

			Map<String, String> valueMap = (Map<String, String>) newImage.get("value");
			Object value = valueMap.entrySet().stream().map(entry -> {
				if (entry.getKey().equals("N")){
					return Integer.parseInt(entry.getValue());
				} else {
					return entry.getValue();
				}
			}).findFirst().orElseThrow(() -> new RuntimeException());

			if (eventName.equals("INSERT")){

				DynamoDbItem item = new DynamoDbItem(UUID.randomUUID().toString(), key,
						DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()), Map.of("key", key, "value", value));

				table.putItem(item);

			} else if(eventName.equals("MODIFY")){
				Map<String, Object> oldImage = (Map<String, Object>) dynamodb.get("OldImage");

				Map<String, Object> oldImageKeyMap = (Map<String, Object>) oldImage.get("key");
				String oldKey = (String) oldImageKeyMap.values().stream().findFirst().orElse("");

				Map<String, String> oldImageValueMap = (Map<String, String>) oldImage.get("value");
				Object oldValue = oldImageValueMap.entrySet().stream().map(entry -> {
					if (entry.getKey().equals("N")){
						return Integer.parseInt(entry.getValue());
					} else {
						return entry.getValue();
					}
				}).findFirst().orElse(null);

				DynamoDbItem item = new DynamoDbItem(UUID.randomUUID().toString(), key,
						DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()), "value",
						value, oldValue);

				table.putItem(item);
			}


		});

		return null;
	}
}
