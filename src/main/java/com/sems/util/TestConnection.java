package com.sems.util;

import com.sems.dao.EvaluationDAO;
import com.sems.model.Evaluation;
import java.util.List;

public class TestConnection {

    public static void main(String[] args) {
        EvaluationDAO dao = new EvaluationDAO();

        // --- STEP 1: TEST DATABASE INSERT ---
        System.out.println("DEBUG: Testing SQL Insertion...");
        Evaluation testEval = new Evaluation();
        testEval.setStudentId(99); // Ensure student 99 exists in students table
        testEval.setCourseId(1);   // Ensure course 1 exists in courses table
        testEval.setRating(5);
        testEval.setComments("Test from TestConnection.java");

        boolean insertResult = dao.submitEvaluation(testEval);
        System.out.println("RESULT: Was insertion successful? -> " + insertResult);

        // --- STEP 2: TEST RETRIEVING SUBMITTED IDS ---
        System.out.println("\nDEBUG: Testing ID retrieval for Student 99...");
        List<Integer> ids = dao.getEvaluatedCourseIds(99);

        if (ids != null && !ids.isEmpty()) {
            System.out.println("RESULT: Found " + ids.size() + " submitted evaluations.");
            System.out.println("IDs found: " + ids);
        } else {
            System.out.println("RESULT: No IDs found. (This is why your badge isn't showing!)");
        }

        // --- STEP 3: TEST REVEAL IDENTITY ---
        System.out.println("\nDEBUG: Testing Reveal Identity Logic...");
        // Use an ID that actually exists in your evaluations table
        String name = dao.revealStudentIdentity(1);
        System.out.println("RESULT: Identity revealed -> " + name);
    }
}
