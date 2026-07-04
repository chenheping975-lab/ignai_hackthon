package cn.ignai.hackathon.control;

import cn.ignai.hackathon.utils.JsonResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("service", "ignai-hackathon-platform");
        data.put("status", "ok");
        JsonResponse.ok(response, data);
    }
}
