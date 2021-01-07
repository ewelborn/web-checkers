package edu.tarleton.welborn.webchess;

import edu.tarleton.welborn.exceptions.NoSuchGameException;
import edu.tarleton.welborn.exceptions.PlayerDoesNotExistInGameException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

@Path("gameUpdate/{gameID}")
public class GameUpdateWS {
    
    @GET
    @Produces("application/json")
    public Response sendGameUpdate(@Context HttpServletRequest request, @PathParam("gameID") String gameID) {
        // Send an update to the client's AJAX request
        // Ensure that the gameID belongs to an active game
        try {
            Game game = GameManager.getGameFromID(gameID);
            HttpSession session = request.getSession();
            return Response.ok(game.sendGameUpdate(session.getId())).build();
        } catch(NoSuchGameException e) {
            return Response.noContent().build();
        }
    }
    
    @POST
    @Produces("text/plain")
    public Response receiveGameUpdate(@Context HttpServletRequest request, @PathParam("gameID") String gameID, Form form) {
        // Receive a game update from the client
        // Ensure that the gameID belongs to an active game
        try {
            Game game = GameManager.getGameFromID(gameID);
            HttpSession session = request.getSession();
            game.receiveGameUpdate(game.getPlayerFromSessionToken(session.getId()),form.asMap());
            return Response.ok().build();
        } catch(NoSuchGameException ex) {
            return Response.noContent().build();
        } catch (PlayerDoesNotExistInGameException ex) {
            return Response.noContent().build();
        }
    }
    
}