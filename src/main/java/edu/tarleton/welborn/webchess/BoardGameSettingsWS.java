package edu.tarleton.welborn.webchess;

import edu.tarleton.welborn.exceptions.NoSuchGameException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("gameSettings/{gameName}")
public class BoardGameSettingsWS {
    
    @GET
    @Produces("application/json")
    public Response sendGameSettings(@Context HttpServletRequest request, @PathParam("gameName") String gameName) {
        BoardGame boardGame = GameManager.getBoardGames().get(gameName);
        if(boardGame != null) {
            return Response.ok(boardGame.getBoardGameSettings()).build();
        } else {
            return Response.noContent().build();
        }
    }
    
}