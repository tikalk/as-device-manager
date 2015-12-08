package com.tikal.angelsense.devicemanager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.cyngn.kafka.MessageProducer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerRequest;

public class HttpGpsGatewayVerticle extends AbstractVerticle {
	private final SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");

	private boolean createReceptionTime;

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HttpGpsGatewayVerticle.class);

	@Override
	public void start() {
		vertx.createHttpServer().requestHandler(req -> req.bodyHandler(b -> handleGps(req, b.toString()))).listen(config().getInteger("http-port"));
		logger.info("Started HTTP Server on port {} to listen for GPS", config().getInteger("http-port"));
	}

	private void handleGps(final HttpServerRequest req, final String gpsPayload) {
		final String identifiedPayload = addIdAndReceptionTime(gpsPayload);
		logger.debug("Sending the GPS Reading:{}", identifiedPayload);
		vertx.eventBus().send(MessageProducer.EVENTBUS_DEFAULT_ADDRESS, identifiedPayload);
		req.response().end();
	}

	private String addIdAndReceptionTime(final String gpsPayload) {
		String identifiedPauload;
		if(createReceptionTime)
			identifiedPauload = gpsPayload+","+Long.valueOf(df.format(new Date()));
		identifiedPauload = gpsPayload+","+UUID.randomUUID();
		return identifiedPauload;
	}

}
