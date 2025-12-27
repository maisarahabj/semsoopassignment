package com.sems.servlet;

import com.sems.dao.EvaluationDAO;
import com.sems.dao.EnrollmentDAO;
import com.sems.model.Evaluation;
import com.sems.model.Enrollment;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/EvaluationServlet")
public class EvaluationServlet extends HttpServlet {

    private final EvaluationDAO evalDAO = new EvaluationDAO();
    private final EnrollmentDAO enrollDAO = new EnrollmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if ("student".equals(role)) {
            // Get studentId safely from session
            Integer studentId = (Integer) session.getAttribute("studentId");
            if (studentId != null) {
                // 1. Get courses eligible for evaluation (A, B, C, FAIL)
                List<Enrollment> transcript = enrollDAO.getFullTranscript(studentId);

                // 2. Get IDs of courses this student has ALREADY evaluated
                List<Integer> evaluatedCourseIds = evalDAO.getEvaluatedCourseIds(studentId);

                request.setAttribute("transcript", transcript);
                request.setAttribute("submittedCourseIds", evaluatedCourseIds);
                request.getRequestDispatcher("/student/evaluation.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
            }
        } else if ("admin".equals(role)) {
            // Logic for admin to see course averages
            List<Evaluation> courseStats = evalDAO.getCourseAverages();
            request.setAttribute("courseStats", courseStats);
            request.getRequestDispatcher("/admin/adminviewevals.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("submitEvaluation".equals(action)) {
            handleStudentSubmission(request, response);
        } else if ("revealIdentity".equals(action)) {
            handleAdminReveal(request, response);
        }
    }

    private void handleStudentSubmission(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        try {
            Integer studentId = (Integer) session.getAttribute("studentId");
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comments = request.getParameter("comments");

            // DEBUG PRINT
            System.out.println("SUBMITTING EVAL: Student=" + studentId + ", Course=" + courseId + ", Rating=" + rating);

            Evaluation eval = new Evaluation();
            eval.setStudentId(studentId);
            eval.setCourseId(courseId);
            eval.setRating(rating);
            eval.setComments(comments);

            boolean success = evalDAO.submitEvaluation(eval);
            System.out.println("DATABASE INSERT SUCCESS: " + success);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/EvaluationServlet?status=success");
            } else {
                response.sendRedirect(request.getContextPath() + "/EvaluationServlet?error=fail");
            }
        } catch (Exception e) {
            e.printStackTrace(); // This will show you EXACTLY why it crashed in the output log
            response.sendRedirect(request.getContextPath() + "/EvaluationServlet?error=exception");
        }
    }

    private void handleAdminReveal(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String securityPassword = request.getParameter("securityPassword");
        String evalIdStr = request.getParameter("evalId");

        if (evalIdStr == null || evalIdStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int evalId = Integer.parseInt(evalIdStr);

        // Security check for the reveal action
        if ("admin123".equals(securityPassword)) {
            String name = evalDAO.revealStudentIdentity(evalId);
            response.setContentType("text/plain");
            response.getWriter().write(name);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Incorrect Password");
        }
    }
}
