package com.sems.dao;

import com.sems.model.Evaluation;
import com.sems.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvaluationDAO {

    private static final Logger LOGGER = Logger.getLogger(EvaluationDAO.class.getName());

    /**
     * NEW METHOD: Fixes the Compilation Error in EvaluationServlet. Checks
     * which courses a student has already reviewed.
     */
    public List<Integer> getEvaluatedCourseIds(int studentId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT course_id FROM evaluations WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("course_id"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching evaluated course IDs", e);
        }
        return ids;
    }

    /**
     * STUDENT ACTION: Submit a new evaluation for a course.
     */
    public boolean submitEvaluation(Evaluation eval) {
        String sql = "INSERT INTO evaluations (course_id, student_id, rating, comments) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eval.getCourseId());
            pstmt.setInt(2, eval.getStudentId());
            pstmt.setInt(3, eval.getRating());
            pstmt.setString(4, eval.getComments());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error submitting evaluation", e);
            return false;
        }
    }

    /**
     * ADMIN VIEW: Get list of courses with their average ratings.
     */
    public List<Evaluation> getCourseAverages() {
        List<Evaluation> list = new ArrayList<>();
        // Modified query to actually pull the average into the rating field
        String sql = "SELECT c.course_id, c.course_code, c.course_name, "
                + "IFNULL(AVG(e.rating), 0) as avg_rating, COUNT(e.evaluation_id) as review_count "
                + "FROM courses c "
                + "LEFT JOIN evaluations e ON c.course_id = e.course_id "
                + "GROUP BY c.course_id";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Evaluation eval = new Evaluation();
                eval.setCourseId(rs.getInt("course_id"));
                eval.setCourseName(rs.getString("course_name"));
                // Store the rounded average in the rating field for the star display
                eval.setRating((int) Math.round(rs.getDouble("avg_rating")));
                list.add(eval);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching course averages", e);
        }
        return list;
    }

    // ... (getReviewsByCourseId and revealStudentIdentity remain the same)
    public List<Evaluation> getReviewsByCourseId(int courseId) {
        List<Evaluation> reviews = new ArrayList<>();
        String sql = "SELECT evaluation_id, rating, comments, submitted_date FROM evaluations WHERE course_id = ? ORDER BY submitted_date DESC";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Evaluation e = new Evaluation();
                e.setEvaluationId(rs.getInt("evaluation_id"));
                e.setRating(rs.getInt("rating"));
                e.setComments(rs.getString("comments"));
                e.setSubmittedDate(rs.getTimestamp("submitted_date"));
                reviews.add(e);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching course reviews", e);
        }
        return reviews;
    }

    public String revealStudentIdentity(int evaluationId) {
        String sql = "SELECT s.first_name, s.last_name FROM students s "
                + "JOIN evaluations e ON s.student_id = e.student_id "
                + "WHERE e.evaluation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, evaluationId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("first_name") + " " + rs.getString("last_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Identity Hidden";
    }
}
