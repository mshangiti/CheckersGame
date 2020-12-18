package com.webcheckers;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.ui.WebServer;
import org.junit.Before;
import org.junit.Test;
import spark.TemplateEngine;
import spark.template.freemarker.FreeMarkerEngine;

public class ApplicationTest {

    @Test
    public void test_constructor() {
        // inject the game center and freemarker engine into web server
        // create the one and only game center
        final TemplateEngine templateEngine = new FreeMarkerEngine();
        final GameCenter gameCenter = new GameCenter();
        WebServer webServer = new WebServer(gameCenter,templateEngine);
        new Application(webServer);
    }

    @Test(expected = IllegalStateException.class)
    public void test_main() {
        Application.main(new String[]{""});
    }

}