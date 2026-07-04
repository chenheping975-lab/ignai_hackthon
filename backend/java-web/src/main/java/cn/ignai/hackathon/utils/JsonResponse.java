package cn.ignai.hackathon.utils;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonResponse {
    private static final Gson GSON = new Gson();

    private JsonResponse() {
    }

    public static void ok(HttpServletResponse response, Object data) throws IOException {
        write(response, true, "ok", data);
    }

    public static void fail(HttpServletResponse response, int status, String errorCode, String message)
            throws IOException {
        response.setStatus(status);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("errorCode", errorCode);
        write(response, false, message, data);
    }

    private static void write(HttpServletResponse response, boolean success, String message, Object data)
            throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("success", success);
        body.put("message", message);
        body.put("data", data);
        response.getWriter().write(GSON.toJson(body));
    }
}
