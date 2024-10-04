package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import org.example.OpenMeteoClient;

@LambdaHandler(
    lambdaName = "api_handler",
	roleName = "api_handler-role",
	layers = {"sdk_layer"},
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaLayer(
		layerName = "sdk_layer",
		libraries = {"lib/open-meteo-client-1.0.jar"},
		runtime = DeploymentRuntime.JAVA11,
		architectures = {Architecture.X86_64},
		artifactExtension = ArtifactExtension.JAR
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
public class ApiHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent requestEvent, Context context) {
		String method = requestEvent.getRequestContext().getHttp().getMethod();
		String path = requestEvent.getRequestContext().getHttp().getPath();

		if (method.equals("GET") && path.equals("/weather")) {
			OpenMeteoClient client = new OpenMeteoClient();

			return APIGatewayV2HTTPResponse.builder()
					.withStatusCode(200)
					.withBody(client.getWeather())
					.build();
		}

		return APIGatewayV2HTTPResponse.builder()
				.withStatusCode(404)
				.build();
	}

}
