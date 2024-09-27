package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.RetentionSetting;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@LambdaHandler(
    lambdaName = "uuid_generator",
	roleName = "uuid_generator-role",
		layers = {"sdk-layer"},
	isPublishVersion = true,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaLayer(
		layerName = "sdk-layer",
		libraries = {"lib/gson-2.10.1.jar"},
		runtime = DeploymentRuntime.JAVA11,
		architectures = {Architecture.ARM64},
		artifactExtension = ArtifactExtension.ZIP
)
@RuleEventSource(targetRule = "uuid_trigger")
public class UuidGenerator implements RequestHandler<Object, Void> {

	Gson gson = new Gson();

	public Void handleRequest(Object request, Context context) {
		Content content = new Content();
		content.setIds(IntStream.range(0, 10).boxed().map(i -> UUID.randomUUID().toString())
				.collect(Collectors.toList())
		);

		S3Client s3Client = S3Client.create();

		
		// write String to file
		try {
			Path path = Path.of("/tmp/", DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()));
			Files.write(path, gson.toJson(content).getBytes());
			s3Client.putObject(PutObjectRequest.builder()
					.bucket("cmtr-326d7d29-uuid-storage-test")
					.key(DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()))
					.build(), path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	private class Content {
		private List<String> ids;

		public Content() {
		}

		public List<String> getIds() {
			return ids;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}
	}
}


