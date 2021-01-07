package edu.tarleton.welborn.webchess;

import edu.tarleton.welborn.exceptions.NoSuchBoardGameException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "GameCreationServlet", urlPatterns = {"/creategame"})
public class GameCreationServlet extends HttpServlet {

    private void displayError(HttpServletRequest request, HttpServletResponse response, String error) throws ServletException, IOException {
        request.setAttribute("error",error);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("WEB-INF/error.jsp");
        requestDispatcher.forward(request,response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Game game = GameManager.startGame(request.getParameter("boardGame"),request.getParameterMap());
            response.sendRedirect("joingame?gameID="+game.getGameID());
        } catch (NoSuchBoardGameException ex) {
            displayError(request,response,"Board game, " + request.getParameter("boardGame") + ", does not exist!");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }

}
