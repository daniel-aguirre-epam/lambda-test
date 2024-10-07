package com.task10;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;
import com.task10.dto.RouteKey;
import com.task10.handler.GetReservationsHandler;
import com.task10.handler.GetRootHandler;
import com.task10.handler.GetTablesHandler;
import com.task10.handler.PostReservationsHandler;
import com.task10.handler.PostSignInHandler;
import com.task10.handler.PostSignUpHandler;
import com.task10.handler.PostTablesHandler;
import com.task10.handler.RouteNotImplementedHandler;
import org.slf4j.Logger;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.util.Map;

import static com.syndicate.deployment.model.environment.ValueTransformer.USER_POOL_NAME_TO_CLIENT_ID;
import static com.syndicate.deployment.model.environment.ValueTransformer.USER_POOL_NAME_TO_USER_POOL_ID;

@LambdaHandler(
    lambdaName = "api_handler",
	roleName = "api_handler-role",
    runtime = DeploymentRuntime.JAVA17,
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@DependsOn(resourceType = ResourceType.COGNITO_USER_POOL, name = "${booking_userpool}")
@EnvironmentVariables(value = {
        @EnvironmentVariable(key = "REGION", value = "${region}"),
        @EnvironmentVariable(key = "COGNITO_ID", value = "${booking_userpool}", valueTransformer = USER_POOL_NAME_TO_USER_POOL_ID),
        @EnvironmentVariable(key = "CLIENT_ID", value = "${booking_userpool}", valueTransformer = USER_POOL_NAME_TO_CLIENT_ID),
        @EnvironmentVariable(key = "TABLES_TABLE", value = "${tables_table}"),
        @EnvironmentVariable(key = "RESERVATIONS_TABLE", value = "${reservations_table}")
})
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    private final CognitoIdentityProviderClient cognitoClient = null;
    private final Map<RouteKey, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> handlersByRouteKey = null;
    private final Map<String, String> headersForCORS = null;
    private final RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> routeNotImplementedHandler = null;
    private final DynamoDbEnhancedClient dynamoDbClient = null;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ApiHandler.class);

    public ApiHandler() throws JsonProcessingException {
       /* dynamoDbClient = DynamoDbEnhancedClient.create();
        logger.info( new ObjectMapper().writeValueAsString(System.getenv())) ;
        this.cognitoClient = initCognitoClient();
        this.handlersByRouteKey = initHandlers();
        this.headersForCORS = initHeadersForCORS();
        this.routeNotImplementedHandler = new RouteNotImplementedHandler();
*/


    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return getHandler(requestEvent, context)
                .handleRequest(requestEvent, context)
                .withHeaders(headersForCORS);
    }

    private RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getHandler(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return handlersByRouteKey.getOrDefault(getRouteKey(requestEvent, context), routeNotImplementedHandler);
    }

    public static void main(String[] args) throws JsonProcessingException {
        ApiHandler apiHandler = new ApiHandler();
        apiHandler.getRouteKey(new APIGatewayProxyRequestEvent().withPath("/tables/2").withHttpMethod("GET").withRequestContext(new APIGatewayProxyRequestEvent.ProxyRequestContext().withHttpMethod("GET").withPath("/tables/2")),  null);
    }

    private RouteKey getRouteKey(APIGatewayProxyRequestEvent requestEvent, Context context) {
        /*
        context.getLogger().log("Path: " + requestEvent.getPath());
        try {
            context.getLogger().log(new ObjectMapper().writeValueAsString(requestEvent));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
        int beginIndex = requestEvent.getPath().indexOf("/", 1);
        if(beginIndex != -1) {
            String pathWithoutId = requestEvent.getPath().substring(0, requestEvent.getPath().indexOf("/", 1));
            //context.getLogger().log("Path without id: " + pathWithoutId);
            return new RouteKey(requestEvent.getHttpMethod(), pathWithoutId);
        }
        return new RouteKey(requestEvent.getHttpMethod(), requestEvent.getPath());
    }

    private CognitoIdentityProviderClient initCognitoClient() {
        return CognitoIdentityProviderClient.builder()
                .region(Region.of(System.getenv("REGION")))
                .build();
    }

    private Map<RouteKey, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> initHandlers() {
        return Map.of(
                new RouteKey("POST", "/signup"), new PostSignUpHandler(cognitoClient),
                new RouteKey("POST", "/signin"), new PostSignInHandler(cognitoClient),
                new RouteKey("GET", "/tables"), new GetTablesHandler(dynamoDbClient),
                new RouteKey("GET", "/reservations"), new GetReservationsHandler(dynamoDbClient),
                new RouteKey("POST", "/tables"), new PostTablesHandler(dynamoDbClient),
                new RouteKey("POST", "/reservations"), new PostReservationsHandler(dynamoDbClient)
        );
    }

    private Map<String, String> initHeadersForCORS() {
        return Map.of(
                "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
                "Access-Control-Allow-Origin", "*",
                "Access-Control-Allow-Methods", "*",
                "Accept-Version", "*"
        );
    }
}
