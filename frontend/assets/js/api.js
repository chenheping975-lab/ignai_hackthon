const API_BASE = "/api";
const TOKEN_KEY = "ignai_access_token";
const USER_KEY = "ignai_user";

function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function getUser() {
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) return null;

  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

function saveSession(accessToken, user) {
  localStorage.setItem(TOKEN_KEY, accessToken);
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

function requireLogin() {
  if (!getToken()) {
    window.location.href = "./login.html";
    return false;
  }

  return true;
}

async function request(path, options = {}) {
  const headers = {
    Accept: "application/json",
    ...(options.headers || {}),
  };
  const token = getToken();

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  let body = options.body;
  if (body && !(body instanceof FormData)) {
    headers["Content-Type"] = "application/json";
    body = JSON.stringify(body);
  }

  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, {
      ...options,
      headers,
      body,
    });
  } catch {
    throw new Error("无法连接后端服务，请确认 Spring Boot 已启动。");
  }

  let payload;
  try {
    payload = await response.json();
  } catch {
    throw new Error("后端响应不是有效 JSON。");
  }

  if (response.status === 401) {
    clearSession();
    throw new Error("登录已失效，请重新登录。");
  }

  if (!response.ok || payload.success === false) {
    throw new Error(payload.message || `请求失败：${response.status}`);
  }

  return payload.data;
}

const AuthApi = {
  register(data) {
    return request("/auth/register", { method: "POST", body: data });
  },
  login(data) {
    return request("/auth/login", { method: "POST", body: data });
  },
  me() {
    return request("/auth/me");
  },
  logout() {
    return request("/auth/logout", { method: "POST" });
  },
};

const EventApi = {
  current() {
    return request("/public/events/current");
  },
  tracks(eventId) {
    return request(`/public/events/${eventId}/tracks`);
  },
};

const RegistrationApi = {
  submit(data) {
    return request("/registrations", { method: "POST", body: data });
  },
  mine() {
    return request("/registrations/my");
  },
};
