package edu.tarleton.welborn.webchess;

import edu.tarleton.welborn.exceptions.NoSuchGameException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "GameServlet", urlPatterns = {"/GameServlet"})
public class GameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Send an update to the client's AJAX request
        
        // Get the game ID and make sure it belongs to an active game
        String gameID = request.getParameter("gameID");
        try {
            Game game = GameManager.getGameFromID(gameID);
            response.setContentType("application/json");
            String JSON = "{";
        } catch(NoSuchGameException e) {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println("No such game");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Receive an update from the client's AJAX request
    }

}
