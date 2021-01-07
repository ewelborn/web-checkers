package edu.tarleton.welborn.webchess;

import edu.tarleton.welborn.exceptions.NoSuchGameException;
import edu.tarleton.welborn.exceptions.PlayerAlreadyExistsInGameException;
import edu.tarleton.welborn.exceptions.PlayerDoesNotExistInGameException;
import edu.tarleton.welborn.exceptions.PlayerExceedsCapacityOfGame;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "GameInvitationServlet", urlPatterns = {"/joingame"})
public class GameInvitationServlet extends HttpServlet {

    private void displayError(HttpServletRequest request, HttpServletResponse response, String error) throws ServletException, IOException {
        request.setAttribute("error",error);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("WEB-INF/error.jsp");
        requestDispatcher.forward(request,response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Game game = GameManager.getGameFromID(request.getParameter("gameID"));
            HttpSession session = request.getSession();
            Player player = new Player();
            player.setSessionToken(session.getId());
            player.updateLastContact();
            
            try {
                game.addPlayer(player);
            } catch (PlayerAlreadyExistsInGameException | PlayerExceedsCapacityOfGame ex) {
                if(ex instanceof PlayerAlreadyExistsInGameException) {
                    try {
                        // Don't really need to do anything, just send them to the game
                        player = game.getPlayerFromSessionToken(session.getId());
                    } catch (PlayerDoesNotExistInGameException ex1) {
                        // This should never happen, we know that the player exists in the game, because we just had that exception
                    }
                } else if(ex instanceof PlayerExceedsCapacityOfGame) {
                    displayError(request,response,"Game ID "+request.getParameter("gameID")+" is already full. Try contacting the lobby owner if you think someone's taken your spot.");
                    return;
                }
            }
            
            request.setAttribute("game",game);
            request.setAttribute("player",player);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(game.getBoardGame().getURL());
            requestDispatcher.forward(request,response);
        } catch (NoSuchGameException ex) {
            displayError(request,response,"Game ID "+request.getParameter("gameID")+" is not a valid game!");
        }
    }

}