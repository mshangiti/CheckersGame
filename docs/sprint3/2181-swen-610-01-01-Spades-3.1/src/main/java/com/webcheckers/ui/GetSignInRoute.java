package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Challenge;
import com.webcheckers.model.Player;
import spark.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static spark.Spark.halt;
/**
 * The {@code GET /singin} route handler.
 */

public class GetSignInRoute implements TemplateViewRoute {

    //
    // constants
    //
    static final String VIEW_NAME = "signin.ftl";
    static final String TITLE = "Welcome";
    static final String TITLE_ATTR = "title";

    //
    // Attributes
    //
    private final GameCenter gameCenter;

    //
    // Constructor
    //

    GetSignInRoute (final GameCenter gameCenter)
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
        vm.put(TITLE_ATTR, GetSignInRoute.TITLE);
        if(gameCenter.isPlayerLoggedIn(request.session())) {
            //user logged in
            response.redirect(WebServer.HOME_URL);
             halt();
            return null;
            // vm.put(gameCenter.SESSION_USER, gameCenter.getPlayer(request.session()).getName());
            // ArrayList<Player> onlinePlayers = gameCenter.getOnlinePlayers();
            // vm.put(HomeController.PLAYER_LIST_ATTR, onlinePlayers);
            // vm.put(HomeController.PLAYER_COUNT_ATTR, onlinePlayers.size()-1);
            // vm.put(HomeController.PLAYER_CHALLENGE_STATUS, Challenge.State.NO_CHALLENGES);
            // vm.put(HomeController.IS_CHALLENGED_ATTR, false);
            // vm.put(HomeController.OPPONENT_NAME_ATTR, "");
            // return new ModelAndView(vm, HomeController.VIEW_NAME);
        }

        //new player
        return new ModelAndView(vm, VIEW_NAME);
        
    }

}
