package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.Map;

@LambdaHandler(
    lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = false,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaLayer(
		layerName = "sdk-layer",
		libraries = {"lib/gson-2.10.1.jar"},
		runtime = DeploymentRuntime.JAVA11,
		architectures = {Architecture.ARM64},
		artifactExtension = ArtifactExtension.ZIP
)
@LambdaUrlConfig(
	authType = AuthType.NONE,
	invokeMode = InvokeMode.BUFFERED
)
public class HelloWorld implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent apiGatewayV2HTTPEvent, Context context) {
		String method = getMethod(apiGatewayV2HTTPEvent);
		String path = getPath(apiGatewayV2HTTPEvent);

		if (method.equals("GET") && path.equals("/hello")) {
			return buildResponse(200, Body.ok());
		}

		return buildResponse(400, Body.error(String.format("" +
				"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s", path, method)));

			}


	private APIGatewayV2HTTPResponse buildResponse(int statusCode, Object body) {
		return APIGatewayV2HTTPResponse.builder()
				.withStatusCode(statusCode)
				.withBody(gson.toJson(body))
				.build();
	}

	private String getMethod(APIGatewayV2HTTPEvent requestEvent) {
		return requestEvent.getRequestContext().getHttp().getMethod();
	}

	private String getPath(APIGatewayV2HTTPEvent requestEvent) {
		return requestEvent.getRequestContext().getHttp().getPath();
	}


	private static class Body {
		private final String message;
		private final String error;

		public Body(String message, String error) {
			this.message = message;
			this.error = error;
		}

		static Body ok() {
			return new Body("Hello from Lambda", null);
		}

		static Body error(String error) {
			return new Body(null, error);
		}

		public String getMessage() {
			return message;
		}

		public String getError() {
			return error;
		}
	}
}
