package com.github.bedrin.batchy.mock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Used for testing on deveopment machines only
 * todo move to another codebase (separate module or whatever?)
 */
public class MockServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.getWriter().print("Hello World!");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader br = req.getReader();
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        resp.setContentType("text/html");
        resp.getWriter().print("Hello World!");
    }
}
