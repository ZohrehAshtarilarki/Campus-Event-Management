package controller;

import dal.EventDAO;
import dal.ReviewDAO;
import dal.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Event;
import model.User;

import java.io.IOException;


@WebServlet("/SubmitRatingServlet")
public class SubmitRatingServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Create an instance of UserDAO to call the non-static getUserById method.
        UserDAO userDAO = new UserDAO();

        HttpSession session = request.getSession();
        // Retrieve sjsuId from session
        Integer sjsuId = (Integer) session.getAttribute("SJSUID");

        if (sjsuId == null) {
            // Redirect to login page if sjsuId is not available in the session
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        String eventIdParam = request.getParameter("eventId");
        String ratingParam = request.getParameter("rating");

        if (eventIdParam == null || ratingParam == null) {
            request.setAttribute("errorMessage", "Invalid event ID or rating.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/eventInfo.jsp"); // Modified
            dispatcher.forward(request, response);
            return;
        }

        int eventId, rating;

        try {
            eventId = Integer.parseInt(eventIdParam);
            rating = Integer.parseInt(ratingParam);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid event ID or rating format.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/eventInfo.jsp"); // Modified
            dispatcher.forward(request, response);
            return;
        }

        ReviewDAO reviewDAO = new ReviewDAO();
        boolean result = reviewDAO.saveRating(eventId, sjsuId, rating);


        if (result) {
            EventDAO eventDAO = new EventDAO();
            // Fetch the event details again to include in the forwarded request
            Event event = eventDAO.getEventById(eventId);

            if (event != null) {
                // Set the event object in the request scope
                request.setAttribute("event", event);

                // Set a success message
                request.setAttribute("successMessage", "Rating submitted successfully!");
                // Also set the eventId in the request attribute
                request.setAttribute("eventId", eventId);
            } else {
                // Set an error message if event is not found
                request.setAttribute("errorMessage", "Event not found.");
            }
        } else {
            // Set an error message in case of failure in saving rating
            request.setAttribute("errorMessage", "There was an error submitting the rating.");
        }

        // Forward to eventInfo.jsp in all cases
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/eventInfo.jsp");
        dispatcher.forward(request, response);
    }
}