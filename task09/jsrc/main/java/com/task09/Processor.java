package com.task09;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.TracingMode;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import org.example.OpenMeteoClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
    lambdaName = "processor",
	roleName = "processor-role",
	layers = {"sdk_layer"},
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED,
		tracingMode = TracingMode.Active
)
@LambdaLayer(
		layerName = "sdk_layer",
		libraries = {"lib/open-meteo-client-1.0.jar", "lib/gson-2.10.1.jar"},
		runtime = DeploymentRuntime.JAVA11,
		architectures = {Architecture.X86_64},
		artifactExtension = ArtifactExtension.JAR
)
@EnvironmentVariables(value = {
	@EnvironmentVariable(key = "table", value = "${target_table}")
})
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
@DependsOn(
		name = "Weather",
		resourceType = ResourceType.DYNAMODB_TABLE
)
public class Processor implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	Gson gson = new Gson();

	private DynamoDbEnhancedClient dynamoDbClient;
	private DynamoDbTable<WeatherForecast> table;


	public Processor() {
		dynamoDbClient = DynamoDbEnhancedClient.create();
		table = dynamoDbClient.table("cmtr-326d7d29-Weather-test", TableSchema.fromBean(WeatherForecast.class));
	}

	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent requestEvent, Context context) {
		String method = requestEvent.getRequestContext().getHttp().getMethod();
		String path = requestEvent.getRequestContext().getHttp().getPath();

		context.getLogger().log("Method: " + method);
		context.getLogger().log("Path: " + path);

		if (method.equals("GET") && path.equals("/weather")) {
			OpenMeteoClient client = new OpenMeteoClient();
			String weatherInfoInJson = client.getWeather();
			WeatherResponse weatherResponse = gson.fromJson(weatherInfoInJson, WeatherResponse.class);

			WeatherForecast.Forecast forecast = new WeatherForecast.Forecast(weatherResponse.getElevation(),
					weatherResponse.getGenerationtimeMs(), weatherResponse.getHourly(), weatherResponse.getHourlyUnits(),
					weatherResponse.getLatitude(), weatherResponse.getLongitude(), weatherResponse.getTimezone(),
					weatherResponse.getTimezoneAbbreviation(), weatherResponse.getUtcOffsetSeconds());

			WeatherForecast weatherForecast = new WeatherForecast();
			weatherForecast.setForecast(forecast);
			weatherForecast.setId(UUID.randomUUID().toString());
			context.getLogger().log("Weather forecast: " + weatherForecast);
			table.putItem(weatherForecast);
			context.getLogger().log("Weather forecast saved to the table");
		}

		return  APIGatewayV2HTTPResponse.builder()
				.withStatusCode(200)
				.build();
	}

}