package cn.ignai.hackathon.control;

import cn.ignai.hackathon.utils.JsonResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProjectServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("module", "projects");
        data.put("videoSupported", false);
        JsonResponse.ok(response, data);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("projectStatus", "draft");
        data.put("videoSupported", false);
        JsonResponse.ok(response, data);
    }
}
