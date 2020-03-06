package com.github.quickcougar.smart_fridge.modules;

import com.github.quickcougar.smart_fridge.handlers.LoggingHandler;
import com.google.inject.AbstractModule;

public class AdminModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LoggingHandler.class);
    }
}
