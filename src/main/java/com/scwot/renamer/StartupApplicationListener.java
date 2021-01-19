package com.scwot.renamer;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListener {

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }
}
