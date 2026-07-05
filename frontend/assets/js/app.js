const ribbons = document.querySelector(".signal-strip div");

if (ribbons) {
  ribbons.innerHTML += ribbons.innerHTML;
}

const timelineItems = document.querySelectorAll(".timeline-item");

const observer = new IntersectionObserver(
  (entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add("is-visible");
      }
    });
  },
  { threshold: 0.35 },
);

timelineItems.forEach((item) => observer.observe(item));

const API_BASE = "/api";
const TOKEN_KEY = "ignai_access_token";

function hasToken() {
  return Boolean(localStorage.getItem(TOKEN_KEY));
}

function apiUrl(path) {
  return `${API_BASE}${path}`;
}

async function fetchJson(path, options = {}) {
  const headers = {
    Accept: "application/json",
    ...(options.headers || {}),
  };
  const token = localStorage.getItem(TOKEN_KEY);

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  let body = options.body;
  if (body && !(body instanceof FormData)) {
    headers["Content-Type"] = "application/json";
    body = JSON.stringify(body);
  }

  const response = await fetch(apiUrl(path), {
    ...options,
    headers,
    body,
  });
  const payload = await response.json();

  if (!response.ok || payload.success === false) {
    throw new Error(payload.message || `请求失败：${response.status}`);
  }

  return payload.data;
}

function bindBusinessEntryLinks() {
  document.querySelectorAll('a[href="#apply"], .hero-actions .primary-button').forEach((link) => {
    link.addEventListener("click", (event) => {
      event.preventDefault();

      if (!hasToken()) {
        window.location.href = "./login.html";
        return;
      }

      document.querySelector("#apply")?.scrollIntoView({ behavior: "smooth", block: "start" });
    });
  });

  document.querySelector("#apply .primary-button")?.addEventListener("click", async (event) => {
    event.preventDefault();

    if (!hasToken()) {
      window.location.href = "./login.html";
      return;
    }

    await submitRegistration();
  });
}

function safeText(selector, value) {
  const target = document.querySelector(selector);
  if (target && value) {
    target.textContent = value;
  }
}

function renderProjectCard(project) {
  return `
    <article class="project-card">
      <img src="${project.coverUrl || "./assets/img/local-global-embers.png"}" alt="${project.title || "Project"}" />
      <div>
        <span>${project.trackName || project.trackId ? `Track ${project.trackName || project.trackId}` : "Project"}</span>
        <h3>${project.title || "未命名作品"}</h3>
        <p>${project.tagline || project.description || ""}</p>
        <footer><strong>${project.averageScore ?? "-"}</strong><small>${project.ratingCount || 0} votes</small></footer>
      </div>
    </article>
  `;
}

async function hydrateCurrentEvent() {
  try {
    const event = await fetchJson("/public/events/current");
    safeText(".hero h1", event?.title);
    safeText(".hero-lead", event?.description);

    const meta = document.querySelectorAll(".event-meta strong");
    if (meta[0] && event?.location) meta[0].textContent = event.location;
    if (meta[2]) meta[2].textContent = event?.registrationOpen === false ? "报名暂未开放" : "开放报名";
  } catch (error) {
    console.info("保留静态活动内容：", error.message);
  }
}

async function hydrateShowcase() {
  try {
    const data = await fetchJson("/public/projects?status=submitted&pageSize=2");
    const list = Array.isArray(data) ? data : data?.list;
    const grid = document.querySelector("#showcase .project-grid");

    if (grid && list?.length) {
      grid.innerHTML = list.slice(0, 2).map(renderProjectCard).join("");
    }
  } catch (error) {
    console.info("保留静态作品内容：", error.message);
  }
}

async function submitRegistration() {
  const applyButton = document.querySelector("#apply .primary-button");
  const originalText = applyButton?.textContent || "加入 IGNAI";

  try {
    if (applyButton) {
      applyButton.textContent = "正在提交...";
      applyButton.setAttribute("aria-busy", "true");
    }

    const event = await fetchJson("/public/events/current");
    const user = await fetchJson("/auth/me");

    await fetchJson("/registrations", {
      method: "POST",
      body: {
        eventId: event?.id || 1,
        registrationType: "individual",
        teamName: null,
        contactName: user?.name || "",
        contactPhone: user?.phone || "",
        contactEmail: user?.email || null,
        trackId: null,
      },
    });

    if (applyButton) {
      applyButton.textContent = "已提交报名";
    }
    window.alert("报名提交成功，等待审核。");
  } catch (error) {
    if (applyButton) {
      applyButton.textContent = originalText;
    }
    window.alert(error.message);
  } finally {
    applyButton?.removeAttribute("aria-busy");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  bindBusinessEntryLinks();
  hydrateCurrentEvent();
  hydrateShowcase();
});
