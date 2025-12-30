/**
 *
 * @author maisarahabjalil
 *
 *  STUDENT VIEW:
 *  doGet   -   shows list of completed course
 *          -   checks submitted reviews
 *  ADMIN VIEW:
 *  doPost  -   displays summary w ratings
 *          -   fetches comments
 *
 *
 */
package com.sems.servlet;

import com.sems.dao.ActivityLogDAO;
import com.sems.dao.EvaluationDAO;
import com.sems.dao.EnrollmentDAO;
import com.sems.dao.StudentDAO;
import com.sems.model.Student;
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
    private final StudentDAO studentDAO = new StudentDAO();
    private final ActivityLogDAO logDAO = new ActivityLogDAO(); // 1. Log DAO initialized

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        Integer userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");

        // 1. AJAX Check: For the Admin "Eval" pop-up
        if ("getReviews".equals(action)) {
            handleGetReviews(request, response);
            return;
        }

        // 2. Security Check
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 3. Student View Logic
        if ("student".equals(role)) {
            Integer studentId = (Integer) session.getAttribute("studentId");

            if (studentId != null) {
                Student student = studentDAO.getStudentByUserId(userId);
                List<Enrollment> transcript = enrollDAO.getFullTranscript(studentId);
                List<Integer> evaluatedCourseIds = evalDAO.getEvaluatedCourseIds(studentId);

                request.setAttribute("student", student);
                request.setAttribute("transcript", transcript);
                request.setAttribute("submittedCourseIds", evaluatedCourseIds);

                request.getRequestDispatcher("/student/evaluation.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
            }

            // 4. Admin View Logic
        } else if ("admin".equals(role)) {
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
        Integer userId = (Integer) session.getAttribute("userId"); // Needed for log
        Integer studentId = (Integer) session.getAttribute("studentId");

        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comments = request.getParameter("comments");

            Evaluation eval = new Evaluation();
            eval.setStudentId(studentId);
            eval.setCourseId(courseId);
            eval.setRating(rating);
            eval.setComments(comments);

            boolean success = evalDAO.submitEvaluation(eval);

            if (success) {
                // 2. TRIGGER LOG: Record student evaluation
                logDAO.recordLog(userId, courseId, "EVALUATE",
                        "Student submitted a " + rating + "-star evaluation for course ID: " + courseId);

                response.sendRedirect(request.getContextPath() + "/EvaluationServlet?status=success");
            } else {
                response.sendRedirect(request.getContextPath() + "/EvaluationServlet?error=fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/EvaluationServlet?error=exception");
        }
    }

    private void handleAdminReveal(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        Integer adminUserId = (Integer) session.getAttribute("userId"); // The Admin performing the reveal
        String securityPassword = request.getParameter("securityPassword");
        String evalIdStr = request.getParameter("evalId");

        if (evalIdStr == null || evalIdStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int evalId = Integer.parseInt(evalIdStr);

        // Security check for identity reveal
        if ("admin123".equals(securityPassword)) {
            String studentName = evalDAO.revealStudentIdentity(evalId);

            // 3. TRIGGER LOG: Record that an Admin peeked at an identity
            logDAO.recordLog(adminUserId, evalId, "ADMIN_REVEAL_IDENTITY",
                    "Admin revealed student identity for Evaluation ID: " + evalId);

            response.setContentType("text/plain");
            response.getWriter().write(studentName);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Incorrect Password");
        }
    }

    private void handleGetReviews(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            List<Evaluation> reviews = evalDAO.getReviewsByCourseId(courseId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < reviews.size(); i++) {
                Evaluation r = reviews.get(i);
                json.append("{");
                json.append("\"rating\":").append(r.getRating()).append(",");

                String cleanComment = r.getComments() != null
                        ? r.getComments().replace("\"", "\\\"").replace("\n", " ").replace("\r", "") : "";

                json.append("\"comments\":\"").append(cleanComment).append("\",");
                json.append("\"submittedDate\":\"").append(r.getSubmittedDate()).append("\",");
                json.append("\"evalId\":").append(r.getEvaluationId());
                json.append("}");

                if (i < reviews.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");

            response.getWriter().write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("[]");
        }
    }
}
