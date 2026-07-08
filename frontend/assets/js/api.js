const API_BASE = "/api";
const TOKEN_KEY = "ignai_access_token";
const USER_KEY = "ignai_user";

// ── axios 实例 ──
const apiClient = axios.create({
  baseURL: API_BASE,
  headers: {
    Accept: "application/json",
  },
});

// ── 请求拦截器：自动附加 Token 与 Content-Type ──
apiClient.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  if (config.data && !(config.data instanceof FormData)) {
    config.headers["Content-Type"] = "application/json";
  }
  return config;
});

// ── Session 工具 ──
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

// ── 通用请求函数 ──
async function request(path, options = {}) {
  let response;
  try {
    response = await apiClient({
      url: path,
      method: options.method || "GET",
      data: options.body,
      // 透传其他 axios 支持的选项（params、timeout 等）
      ...options,
      // 清除 fetch 专用字段
      body: undefined,
      headers: undefined,
    });
  } catch (error) {
    if (error.response) {
      // 服务端返回了错误状态码
      if (error.response.status === 401) {
        clearSession();
        throw new Error("登录已失效，请重新登录。");
      }
      const payload = error.response.data;
      throw new Error(payload.message || `请求失败：${error.response.status}`);
    }
    // 网络错误 / 无法连接
    throw new Error("无法连接后端服务，请确认 Spring Boot 已启动。");
  }

  const payload = response.data;
  if (payload.success === false) {
    throw new Error(payload.message || "请求失败");
  }

  return payload.data;
}

// ── API 命名空间 ──
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
  list(page = 1, pageSize = 6) {
    return request(`/public/events?page=${page}&pageSize=${pageSize}`);
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
