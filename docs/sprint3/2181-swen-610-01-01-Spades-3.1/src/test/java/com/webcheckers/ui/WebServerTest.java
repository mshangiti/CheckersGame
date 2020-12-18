package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import org.junit.Test;
import spark.TemplateEngine;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class WebServerTest {

    @Test
    public void initialize() {

        TemplateEngine templateEngine = mock(TemplateEngine.class);
        final GameCenter gameCenter = new GameCenter();
        WebServer cut = new WebServer(gameCenter,templateEngine);
        cut.initialize();
        assertNotNull(cut);
    }

}
