package cn.ignai.hackathon.control;

import cn.ignai.hackathon.service.RegistrationService;
import cn.ignai.hackathon.service.impl.RegistrationServiceImpl;
import cn.ignai.hackathon.utils.JsonResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationServlet extends HttpServlet {
    private final RegistrationService registrationService = new RegistrationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("title", "IGNAI AI Skillathon");
        data.put("officialSiteUrl", "https://www.ignai.cn/");
        data.put("benchmarkSiteUrl", "https://aihacktour.com/products");
        data.put("registrationOpen", true);
        JsonResponse.ok(response, data);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long registrationId = registrationService.createFromRequest(request);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("registrationId", registrationId);
        data.put("status", "pending");
        JsonResponse.ok(response, data);
    }
}
