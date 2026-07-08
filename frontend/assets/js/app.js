const ribbons = document.querySelector(".signal-strip div");

if (ribbons) {
  // 首尾拼接无空隙：把内容复制一份，让 marquee 平移 -50% 即可无缝循环
  ribbons.innerHTML += ribbons.innerHTML;
}

/* ============================================================
   主题切换（dark / light，localStorage 持久化）
   ============================================================ */

const THEME_KEY = "ignai-theme";

function getStoredTheme() {
  try {
    return localStorage.getItem(THEME_KEY);
  } catch {
    return null;
  }
}

function getSystemTheme() {
  return window.matchMedia &&
    window.matchMedia("(prefers-color-scheme: light)").matches
    ? "light"
    : "dark";
}

function applyTheme(theme) {
  const root = document.documentElement;
  root.classList.remove("dark", "light");
  root.classList.add(theme === "light" ? "light" : "dark");
  try {
    localStorage.setItem(THEME_KEY, theme);
  } catch {}
  const toggle = document.getElementById("theme-toggle");
  if (toggle) {
    toggle.setAttribute(
      "aria-label",
      theme === "dark" ? "切换到亮色模式" : "切换到暗色模式"
    );
  }
}

function initThemeToggle() {
  const toggle = document.getElementById("theme-toggle");
  if (!toggle) return;
  // 同步当前状态到按钮 label（首次进入时）
  const current = getStoredTheme() || getSystemTheme();
  applyTheme(current === "light" ? "light" : "dark");
  toggle.addEventListener("click", () => {
    const isDark = document.documentElement.classList.contains("dark");
    applyTheme(isDark ? "light" : "dark");
  });
  // 跟随系统变化（仅当用户未手动选择时）
  window
    .matchMedia("(prefers-color-scheme: light)")
    .addEventListener?.("change", (event) => {
      if (!getStoredTheme()) {
        applyTheme(event.matches ? "light" : "dark");
      }
    });
}

const DEMO_REGISTRATION_KEY = "ignai_demo_registration";
// 后端 API 默认启用，除非显式设为 "0" 或 window 变量设为 false
const ENABLE_PUBLIC_API =
  window.IGNAI_ENABLE_PUBLIC_API !== false &&
  localStorage.getItem("ignai_enable_public_api") !== "0";
const ENABLE_REGISTRATION_API =
  window.IGNAI_ENABLE_REGISTRATION_API !== false &&
  localStorage.getItem("ignai_enable_registration_api") !== "0";

const demoTabs = {
  signup: {
    title: "报名",
    html: `
      <form class="demo-form" id="demo-signup-form">
        <label>报名类型
          <select id="demo-registration-type">
            <option value="individual">个人报名</option>
            <option value="team">团队报名</option>
          </select>
        </label>
        <label>队伍名 / 项目方向
          <input id="demo-team-name" type="text" placeholder="个人报名可留空，团队报名填写队伍名" />
        </label>
        <label>关注赛道
          <select id="demo-track">
            <option value="">待现场匹配</option>
            <option value="agent">Agent 工具</option>
            <option value="education">教育实训</option>
            <option value="content">内容生产</option>
            <option value="business">商业应用</option>
          </select>
        </label>
        <div class="demo-row">
          <strong>AI 整理</strong>
          <span>提交后生成技能标签、组队摘要和审核辅助信息。</span>
          <small>可编辑</small>
        </div>
      </form>
    `,
  },
  project: {
    title: "作品",
    html: `
      <div class="demo-row">
        <strong>作品资料</strong>
        <span>项目标题、简介、Demo 链接、代码链接、PPT / HTML / MP3 / 图片附件。</span>
        <small>无视频</small>
      </div>
      <div class="demo-row">
        <strong>提交状态</strong>
        <span id="project-demo-status">等待队长提交，截止后自动锁定。</span>
        <small>draft</small>
      </div>
      <div class="demo-row">
        <strong>曝光页面</strong>
        <span>提交后进入作品展示页，支持赛道筛选、评分和赛后回看。</span>
        <small>public</small>
      </div>
    `,
  },
  rating: {
    title: "评分",
    html: `
      <div class="demo-row">
        <strong>开放评分</strong>
        <span>不设置独立评委身份，参赛者、现场观众或公开访客按规则评分。</span>
        <small>1-5</small>
      </div>
      <div class="score-buttons" aria-label="评分 Demo">
        <button type="button" data-score="1">1</button>
        <button type="button" data-score="2">2</button>
        <button type="button" data-score="3">3</button>
        <button type="button" data-score="4">4</button>
        <button type="button" data-score="5">5</button>
      </div>
      <div class="demo-row">
        <strong>当前样例</strong>
        <span id="score-demo-text">选择一个分数，模拟公开评分记录。</span>
        <small>live</small>
      </div>
    `,
  },
  feishu: {
    title: "飞书",
    html: `
      <div class="demo-row">
        <strong>报名 Base</strong>
        <span>报名信息本地保存，同时可同步到飞书 Base 作为协作视图。</span>
        <small>sync</small>
      </div>
      <div class="demo-row">
        <strong>附件备份</strong>
        <span>附件原件本地备份，飞书可保存 Drive 文件或 metadata。</span>
        <small>drive</small>
      </div>
      <div class="demo-row">
        <strong>失败重试</strong>
        <span>同步失败不阻断主流程，进入队列等待管理员重试。</span>
        <small>retry</small>
      </div>
    `,
  },
};

function escapeHtml(value) {
  return String(value ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

function setFeedback(message, tone = "muted") {
  const target = document.getElementById("apply-feedback");
  if (!target) return;

  target.textContent = message;
  target.classList.remove("success", "error");
  if (tone === "success" || tone === "error") {
    target.classList.add(tone);
  }
}

function isLoggedIn() {
  return Boolean(getToken());
}

function currentUser() {
  return getUser();
}

function userDisplayName(user) {
  return user?.name || user?.phone || user?.email || "参赛者";
}

function renderAuthState() {
  const widget = document.getElementById("auth-widget");
  const role = document.getElementById("auth-role");
  const name = document.getElementById("auth-name");
  const sessionLabel = document.getElementById("demo-session-label");
  const loginSwitchButton = document.getElementById("login-or-switch-button");
  const user = currentUser();

  if (isLoggedIn() && user) {
    widget?.setAttribute("data-state", "authed");
    if (role) role.textContent = user.role === "admin" ? "管理员账号" : "参赛账号";
    if (name) name.textContent = userDisplayName(user);
    if (sessionLabel) sessionLabel.textContent = `${userDisplayName(user)} · 已登录`;
    if (loginSwitchButton) loginSwitchButton.textContent = "切换账号";
    setFeedback("已登录，可以提交报名或继续查看作品流程。");
    return;
  }

  widget?.setAttribute("data-state", "guest");
  if (name) name.textContent = "未登录";
  if (role) role.textContent = "访客预览";
  if (sessionLabel) sessionLabel.textContent = "未登录预览";
  if (loginSwitchButton) loginSwitchButton.textContent = "登录 / 切换账号";
}

async function logout({ redirect = false } = {}) {
  clearSession();
  localStorage.removeItem(DEMO_REGISTRATION_KEY);
  renderAuthState();
  renderDemoTab("signup");
  setFeedback("已退出当前账号。", "success");
  if (redirect) {
    window.location.href = "./login.html";
  }
}

function switchAccount() {
  clearSession();
  window.location.href = "./login.html?switch=1";
}

function bindAuthActions() {
  document.getElementById("logout-button")?.addEventListener("click", () => logout());
  document.getElementById("switch-account-button")?.addEventListener("click", switchAccount);
  document.getElementById("login-or-switch-button")?.addEventListener("click", () => {
    if (isLoggedIn()) {
      switchAccount();
      return;
    }
    window.location.href = "./login.html";
  });
}

function bindBusinessEntryLinks() {
  document.querySelectorAll('a[href="#apply"], .hero-actions .primary-button').forEach((link) => {
    link.addEventListener("click", (event) => {
      event.preventDefault();
      document.querySelector("#apply")?.scrollIntoView({ behavior: "smooth", block: "start" });
      if (!isLoggedIn()) {
        setFeedback("请先登录或注册，再提交报名。当前页面可继续预览流程。", "error");
      }
    });
  });

  document
    .getElementById("submit-registration-button")
    ?.addEventListener("click", submitRegistration);
}

function safeText(selector, value) {
  const target = document.querySelector(selector);
  if (target && value) {
    target.textContent = value;
  }
}

function renderProjectCard(project) {
  const title = escapeHtml(project.title || "未命名作品");
  const cover = escapeHtml(project.coverUrl || "./assets/img/local-global-embers.png");
  const track = escapeHtml(project.trackName || (project.trackId ? `Track ${project.trackId}` : "Project"));
  const description = escapeHtml(project.tagline || project.description || "作品简介待完善。");
  const score = escapeHtml(project.averageScore ?? "-");
  const votes = escapeHtml(project.ratingCount || 0);

  return `
    <article class="project-card">
      <img src="${cover}" alt="${title}" />
      <div>
        <span>${track}</span>
        <h3>${title}</h3>
        <p>${description}</p>
        <footer><strong>${score}</strong><small>${votes} votes</small></footer>
      </div>
    </article>
  `;
}

function renderDemoTab(tabName) {
  const config = demoTabs[tabName] || demoTabs.signup;
  const panel = document.getElementById("demo-panel");

  document.querySelectorAll("[data-demo-tab]").forEach((button) => {
    button.classList.toggle("active", button.dataset.demoTab === tabName);
  });

  if (panel) {
    panel.classList.remove("is-switching");
    // 强制重排，让动画重新触发
    void panel.offsetWidth;
    panel.innerHTML = config.html;
    panel.classList.add("is-switching");
  }

  hydrateDemoFromStorage();
}

function bindDemoTabs() {
  document.querySelectorAll("[data-demo-tab]").forEach((button) => {
    button.addEventListener("click", () => renderDemoTab(button.dataset.demoTab));
  });

  document.getElementById("demo-panel")?.addEventListener("click", (event) => {
    const scoreButton = event.target.closest("[data-score]");
    if (!scoreButton) return;

    document.querySelectorAll("[data-score]").forEach((button) => button.classList.remove("active"));
    scoreButton.classList.add("active");
    safeText("#score-demo-text", `已记录样例评分：${scoreButton.dataset.score} 分。`);
  });
}

function hydrateDemoFromStorage() {
  const raw = localStorage.getItem(DEMO_REGISTRATION_KEY);
  if (!raw) return;

  let demo;
  try {
    demo = JSON.parse(raw);
  } catch {
    return;
  }

  const type = document.getElementById("demo-registration-type");
  const teamName = document.getElementById("demo-team-name");
  const track = document.getElementById("demo-track");

  if (type && demo.registrationType) type.value = demo.registrationType;
  if (teamName && demo.teamName) teamName.value = demo.teamName;
  if (track && demo.track) track.value = demo.track;
}

async function hydrateCurrentEvent() {
  if (!ENABLE_PUBLIC_API) return;

  try {
    const event = await EventApi.current();

    // hero 标题
    const heroTitle = document.getElementById("hero-title");
    if (heroTitle && event?.title) heroTitle.textContent = event.title;

    // hero 描述
    const heroDesc = document.getElementById("hero-desc");
    if (heroDesc && event?.description) heroDesc.textContent = event.description;

    // event-meta：城市 / 形式 / 状态
    const meta = document.querySelectorAll(".event-meta strong");
    if (meta[0] && event?.location) meta[0].textContent = event.location;
    if (meta[2]) meta[2].textContent = event?.registrationOpen === false ? "报名暂未开放" : "开放报名";
  } catch (error) {
    console.info("保留静态活动内容：", error.message);
  }
}

async function hydrateShowcase() {
  if (!ENABLE_PUBLIC_API) return;

  try {
    const data = await request("/public/projects?status=submitted&pageSize=2");
    const list = Array.isArray(data) ? data : data?.list;
    const grid = document.querySelector("#showcase .project-grid");

    if (grid && list?.length) {
      grid.innerHTML = list.slice(0, 2).map(renderProjectCard).join("");
    }
  } catch (error) {
    console.info("保留静态作品内容：", error.message);
  }
}

function getDemoRegistrationPayload(event, user) {
  const registrationType = document.getElementById("demo-registration-type")?.value || "individual";
  const track = document.getElementById("demo-track")?.value || "";
  const teamNameInput = document.getElementById("demo-team-name")?.value.trim();
  const teamName = registrationType === "team" ? teamNameInput || "IGNAI Demo Team" : null;

  return {
    eventId: event?.id || 1,
    registrationType,
    teamName,
    contactName: user?.name || "",
    contactPhone: user?.phone || "",
    contactEmail: user?.email || null,
    trackId: track || null,
    track,
  };
}

async function submitRegistration() {
  const button = document.getElementById("submit-registration-button");
  const originalText = button?.textContent || "提交报名 Demo";

  if (!isLoggedIn()) {
    setFeedback("请先登录或注册，再提交报名。", "error");
    window.location.href = "./login.html";
    return;
  }

  try {
    button.textContent = "正在提交...";
    button.setAttribute("aria-busy", "true");

    let event = null;
    if (ENABLE_PUBLIC_API) {
      try {
        event = await EventApi.current();
      } catch (error) {
        console.info("当前活动接口不可用，使用默认活动 ID：", error.message);
      }
    }

    const payload = getDemoRegistrationPayload(event, currentUser());
    localStorage.setItem(DEMO_REGISTRATION_KEY, JSON.stringify(payload));

    if (!ENABLE_REGISTRATION_API) {
      button.textContent = "已保存 Demo 报名";
      setFeedback("后端报名接口仍在补齐，已保存本地 Demo 状态。", "success");
      return;
    }

    try {
      await RegistrationApi.submit(payload);
      button.textContent = "已提交报名";
      setFeedback("报名已提交到后端，等待管理员审核。", "success");
    } catch (error) {
      button.textContent = "已保存 Demo 报名";
      setFeedback(`后端报名接口暂不可用，已保存本地 Demo 状态：${error.message}`, "success");
    }
  } finally {
    button?.removeAttribute("aria-busy");
    setTimeout(() => {
      if (button) button.textContent = originalText;
    }, 1800);
  }
}

function bindRevealMotion() {
  const items = document.querySelectorAll("[data-reveal]");
  if (!items.length) return;

  // 支持 reduced-motion：直接显示
  const prefersReduced = window.matchMedia(
    "(prefers-reduced-motion: reduce)"
  ).matches;
  if (prefersReduced) {
    items.forEach((item) => item.classList.add("is-visible"));
    return;
  }

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("is-visible");
          observer.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.18, rootMargin: "0px 0px -8% 0px" }
  );

  items.forEach((item) => observer.observe(item));
}

function bindCardHoverGlow() {
  // 卡片鼠标位置 → CSS 变量 --mx / --my，触发 radial 光晕跟随
  const cards = document.querySelectorAll(
    ".card-grid article, .action-list article, .project-card"
  );
  cards.forEach((card) => {
    card.addEventListener("pointermove", (event) => {
      const rect = card.getBoundingClientRect();
      const x = ((event.clientX - rect.left) / rect.width) * 100;
      const y = ((event.clientY - rect.top) / rect.height) * 100;
      card.style.setProperty("--mx", `${x}%`);
      card.style.setProperty("--my", `${y}%`);
    });
  });
}

document.addEventListener("DOMContentLoaded", () => {
  initThemeToggle();
  renderAuthState();
  bindAuthActions();
  bindBusinessEntryLinks();
  bindDemoTabs();
  bindRevealMotion();
  bindCardHoverGlow();
  renderDemoTab("signup");
  hydrateCurrentEvent();
  hydrateShowcase();
});
