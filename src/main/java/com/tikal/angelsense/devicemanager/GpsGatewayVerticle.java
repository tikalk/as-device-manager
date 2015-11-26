package com.tikal.angelsense.devicemanager;

import com.cyngn.kafka.MessageProducer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpServerRequest;

public class GpsGatewayVerticle extends AbstractVerticle {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GpsGatewayVerticle.class);

	@Override
	public void start() {
		vertx.deployVerticle(MessageProducer.class.getName(),new DeploymentOptions().setConfig(config()));
		vertx.createHttpServer().requestHandler(req -> req.bodyHandler(b -> handleGps(req, b.toString()))).listen(config().getInteger("http-port"));
		logger.info("Started HTTP Server on port {} to listen for GPS", config().getInteger("http-port"));
	}

	private void handleGps(final HttpServerRequest req, final String gpsPayload) {
		logger.debug("Sending the GPS Reading:{}", gpsPayload);
//		vertx.eventBus().send("gps.all", gpsPayload);
		vertx.eventBus().send(MessageProducer.EVENTBUS_DEFAULT_ADDRESS, gpsPayload);
		req.response().end();
		// vertx.eventBus().send("gps.enrichment", gpsPayload, (final
		// AsyncResult<Message<JsonObject>> ar) ->
		// handleAsyncEnrichment(ar,gpsPayload,req));
	}

}
