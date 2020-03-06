package com.github.quickcougar.smart_fridge.modules;

import com.github.quickcougar.smart_fridge.handlers.FridgeErrorHandler;
import com.google.inject.AbstractModule;
import ratpack.error.ClientErrorHandler;
import ratpack.error.ServerErrorHandler;

public class ErrorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ClientErrorHandler.class).to(FridgeErrorHandler.class);
        bind(ServerErrorHandler.class).to(FridgeErrorHandler.class);
    }
}

