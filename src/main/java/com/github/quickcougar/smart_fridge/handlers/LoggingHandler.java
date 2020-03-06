package com.github.quickcougar.smart_fridge.handlers;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.Request;


public class LoggingHandler implements Handler {
    Logger log = LoggerFactory.getLogger(LoggingHandler.class);

    @Inject
    LoggingHandler() {
    }

    @Override
    public void handle(Context ctx) {
        Request request = ctx.getRequest();
        Blocking.exec(() -> {
            String logMessage = String.format("path=%s method=%s params=%s", request.getPath(),
                    request.getMethod().getName(), request.getQueryParams().toString());
            log.info(logMessage);
        });
        ctx.next();
    }
}
