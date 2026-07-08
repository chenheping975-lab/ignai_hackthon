function setMessage(id, message, tone = "muted") {
  const target = document.getElementById(id);
  if (!target) return;

  const colors = {
    muted: "rgba(32, 21, 14, 0.68)",
    success: "#15803d",
    error: "#b91c1c",
  };
  target.textContent = message;
  target.style.color = colors[tone] || colors.muted;
}

function validPhone(value) {
  return /^1[3-9]\d{9}$/.test(value);
}

function bindLogin() {
  const form = document.getElementById("login-form");
  if (!form) return;

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const account = document.getElementById("account").value.trim();
    const password = document.getElementById("password").value;

    if (!account || !password) {
      setMessage("auth-message", "请填写账号和密码。", "error");
      return;
    }

    setMessage("auth-message", "正在登录...");
    try {
      const data = await AuthApi.login({ account, password });
      if (!data?.accessToken || !data?.user) {
        throw new Error("登录接口需要返回 accessToken 和 user。");
      }

      saveSession(data.accessToken, data.user);
      window.location.href = "./index.html#apply";
    } catch (error) {
      setMessage("auth-message", error.message, "error");
    }
  });
}

function bindRegister() {
  const form = document.getElementById("register-form");
  if (!form) return;

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const name = document.getElementById("name").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirm-password").value;

    if (!name || !phone || !password || !confirmPassword) {
      setMessage("auth-message", "请填写姓名、手机号和密码。", "error");
      return;
    }

    if (!validPhone(phone)) {
      setMessage("auth-message", "请填写有效的中国大陆手机号。", "error");
      return;
    }

    if (password.length < 6) {
      setMessage("auth-message", "密码至少需要 6 位。", "error");
      return;
    }

    if (password !== confirmPassword) {
      setMessage("auth-message", "两次输入的密码不一致。", "error");
      return;
    }

    setMessage("auth-message", "正在注册...");
    try {
      await AuthApi.register({ name, phone, email: email || null, password });
      setMessage("auth-message", "注册成功，请登录。", "success");
      setTimeout(() => {
        window.location.href = "./login.html";
      }, 900);
    } catch (error) {
      setMessage("auth-message", error.message, "error");
    }
  });
}

function renderExistingSessionNotice() {
  const params = new URLSearchParams(window.location.search);
  if (params.get("switch") === "1") {
    clearSession();
    setMessage("auth-message", "已退出当前账号，可以登录新账号。", "success");
    return;
  }

  const user = getUser();
  if (!getToken() || !user) return;

  const card = document.querySelector(".auth-card");
  if (!card || document.querySelector(".auth-session")) return;

  const notice = document.createElement("div");
  notice.className = "auth-session";

  const userBlock = document.createElement("div");
  const label = document.createElement("span");
  const name = document.createElement("strong");
  const clearButton = document.createElement("button");
  const backLink = document.createElement("a");

  label.textContent = "当前已登录";
  name.textContent = user.name || user.phone || user.email || "参赛者";
  clearButton.type = "button";
  clearButton.dataset.authClear = "";
  clearButton.textContent = "切换账号";
  backLink.href = "./index.html#apply";
  backLink.textContent = "返回报名";

  userBlock.append(label, name);
  notice.append(userBlock, clearButton, backLink);
  card.prepend(notice);

  notice.querySelector("[data-auth-clear]")?.addEventListener("click", () => {
    clearSession();
    setMessage("auth-message", "已退出当前账号，可以登录新账号。", "success");
    notice.remove();
  });
}

document.addEventListener("DOMContentLoaded", () => {
  renderExistingSessionNotice();
  bindLogin();
  bindRegister();
});
