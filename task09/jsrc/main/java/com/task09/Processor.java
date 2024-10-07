package com.task09;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Arrays;
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

	public static void main(String[] args) {
		Processor processor = new Processor();
		APIGatewayV2HTTPEvent event = new APIGatewayV2HTTPEvent();
		APIGatewayV2HTTPEvent.RequestContext requestContext = new APIGatewayV2HTTPEvent.RequestContext();
		requestContext.setHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
						.withMethod("GET")
						.withPath("/")
						.build());
		event.setRequestContext(requestContext);

		processor.handleRequest(event, null);
	}

	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent requestEvent, Context context) {
		String method = requestEvent.getRequestContext().getHttp().getMethod();
		String path = requestEvent.getRequestContext().getHttp().getPath();


		if (method.equals("GET") && path.equals("/")) {
			OpenMeteoClient client = new OpenMeteoClient();
			String weatherInfoInJson = client.getWeather();
			try {
				WeatherResponse weatherResponse = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
						.readValue(weatherInfoInJson, WeatherResponse.class);

				Map<String, Object> forecast = Map.of("elevation", weatherResponse.getElevation(),
						"generationtime_ms", weatherResponse.getGenerationtimeMs(),
						"hourly", Map.of(
								"temperature_2m", weatherResponse.getHourly().getTemperature2m(),
								"time", weatherResponse.getHourly().getTime()),
						"hourly_units", Map.of("temperature_2m", weatherResponse.getHourlyUnits().getTemperature2m(),
								"time", weatherResponse.getHourlyUnits().getTime()),
						"latitude", weatherResponse.getLatitude(),
						"longitude", weatherResponse.getLongitude(),
						"timezone", weatherResponse.getTimezone(),
						"timezone_abbreviation", weatherResponse.getTimezoneAbbreviation(),
						"utc_offset_seconds", weatherResponse.getUtcOffsetSeconds());


				WeatherForecast weatherForecast = new WeatherForecast();
				weatherForecast.setForecast(forecast);
				weatherForecast.setId(UUID.randomUUID().toString());
				context.getLogger().log("Weather forecast: " + weatherForecast);
				table.putItem(weatherForecast);
				context.getLogger().log("Weather forecast saved to the table");
			} catch (Exception e) {
				context.getLogger().log("Error: " + e.getMessage() + " " + e.getCause() + " " + Arrays.toString(e.getStackTrace()));
				return APIGatewayV2HTTPResponse.builder()
						.withStatusCode(500)
						.withBody("Internal server error")
						.build();
			}
		}

		return  APIGatewayV2HTTPResponse.builder()
				.withStatusCode(200)
				.build();
	}

}