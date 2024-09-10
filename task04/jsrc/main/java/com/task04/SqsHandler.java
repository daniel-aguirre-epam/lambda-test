package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;

@LambdaHandler(
    lambdaName = "sqs_handler",
	roleName = "sqs_handler-role",
	isPublishVersion = false,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@DependsOn(
		name = "async_queue",
		resourceType = ResourceType.SQS_QUEUE
)
@SqsTriggerEventSource(targetQueue = "async_queue", batchSize = 1)
public class SqsHandler implements RequestHandler<SQSEvent, Void> {

	public Void handleRequest(SQSEvent request, Context context) {
		request.getRecords().forEach(record ->
			context.getLogger().log(record.getBody()));
		return null;
	}
}
