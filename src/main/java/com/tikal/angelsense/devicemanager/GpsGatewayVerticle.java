package com.tikal.angelsense.devicemanager;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

public class GpsGatewayVerticle extends AbstractVerticle {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GpsGatewayVerticle.class);
	
	

	@Override
	public void start() {
		vertx.deployVerticle(new GpsEnrichmentVerticle());
		
		vertx.createHttpServer().requestHandler(req -> req.bodyHandler(b -> handleGps(req, b.toString()))).listen(config().getInteger("http-port"));		
		logger.info("Started HTTP Server on port {} to listen for GPS", config().getInteger("http-port"));
	}

	private void handleGps(final HttpServerRequest req, final String gpsPayload) {
		logger.debug("Sending the GPS Reading:{}", gpsPayload);
		vertx.eventBus().send("gps.enrichment", gpsPayload, (final AsyncResult<Message<JsonObject>> ar) -> handleAsyncEnrichment(ar,gpsPayload,req));
	}

	private void handleAsyncEnrichment(final AsyncResult<Message<JsonObject>> ar, final String gpsPayload, final HttpServerRequest req) {
		if (ar.succeeded()) {
			final JsonObject gps = ar.result().body();
			logger.debug("Got enriched GPS. Send it to both all and specific address channels. GPS is :{}", gps);
			vertx.eventBus().send("gps.all",gps);
			vertx.eventBus().send("gps."+gps.getInteger("angelId"),gps);
			req.response().end();
		}else{
			logger.error("Failed to enrich gps, and thus will not process it. Gps-Payload is {}",gpsPayload);
			req.response().setStatusCode(500).end();
		}
	}

}
