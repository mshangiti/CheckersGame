package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static spark.Spark.halt;
public class GetSignOutRoute implements TemplateViewRoute {

    //
    // constants
    //
    static final String TITLE = "Welcome";

    //
    // Attributes
    //
    private final GameCenter gameCenter;

    //
    // Constructor
    //

    GetSignOutRoute (final GameCenter gameCenter)
    {
        Objects.requireNonNull(gameCenter, "gameCenter must not be null");
        this.gameCenter = gameCenter;
    }

    //
    // TemplateViewRoute method
    //

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView handle(Request request, Response response) {

        // start building the View-Model
        final Map<String, Object> vm = new HashMap<>();
        vm.put("title", GetSignOutRoute.TITLE);

        if(gameCenter.isPlayerLoggedIn(request.session())) {
            //sign out user
            gameCenter.removePlayer(request.session());
            //redirect to sign in page
            return new ModelAndView(vm, GetSignInRoute.VIEW_NAME);
        }else{
            response.redirect(WebServer.SIGNIN_URL);
            halt();
            return null;
        }
        

        
    }
}
