package com.github.quickcougar.smart_fridge.handlers;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;

public class FridgeErrorHandler implements ratpack.error.internal.ErrorHandler {
    Logger log = LoggerFactory.getLogger(FridgeErrorHandler.class);

    @Override
    public void error(Context context, int statusCode) {
        context.getResponse().status(statusCode).send();
    }

    @Override
    public void error(Context context, Throwable throwable) {
        log.error(throwable.getLocalizedMessage());
        log.info(Throwables.getStackTraceAsString(throwable));
        context.getResponse().status(503).send("{\"message\": \"An error occurred\"}");
    }
}
