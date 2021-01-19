package com.scwot.renamer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListener {

    @Value("${app.input}")
    private String input;



    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }
}
