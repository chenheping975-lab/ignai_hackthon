const IGN_EVENT_REGISTRATION_KEY = "ignai_event_registration";
const IGN_PROJECT_LIKES_KEY = "ignai_project_likes";

const hackathonEvents = [
  {
    id: "ignai-ai-skillathon",
    title: "IGNAI AI Skillathon · 长沙",
    status: "registration",
    statusText: "报名开放",
    date: "2026.07.20 - 07.21",
    location: "长沙 · 线下",
    cover: "./assets/img/local-global-embers.png",
    summary: "两天一夜，把真实问题推进到可以被展示、点赞和继续讨论的 AI 作品。",
    href: "./event.html?id=ignai-ai-skillathon",
    projectsHref: "./projects.html?event=ignai-ai-skillathon",
    keywords: ["Agent Tools", "Education", "Content Workflow", "Local Roots", "Global Signal", "No Video"],
    tracks: [
      { name: "Agent 工具", description: "面向工作流、校园、运营和创作场景的 Agent 原型。" },
      { name: "教育实训", description: "围绕课程、作业、项目制学习和成果沉淀做可运行工具。" },
      { name: "内容生产", description: "把现场复盘、图文、音频和知识整理变成更顺手的 AI 流程。" },
      { name: "商业应用", description: "把真实业务问题拆成可以验证的小产品。" },
    ],
    stages: [
      { key: "registration", label: "报名开放", done: true },
      { key: "review", label: "审核组队", done: false },
      { key: "submission", label: "作品提交", done: false },
      { key: "showcase", label: "公开展示", done: false },
    ],
    formFields: [
      {
        key: "name",
        label: "姓名",
        type: "text",
        required: true,
        placeholder: "请填写真实姓名",
      },
      {
        key: "phone",
        label: "手机号",
        type: "text",
        required: true,
        placeholder: "用于接收活动通知",
      },
      {
        key: "registrationType",
        label: "报名类型",
        type: "radio",
        required: true,
        options: ["个人报名", "团队报名"],
      },
      {
        key: "teamName",
        label: "队伍名",
        type: "text",
        required: false,
        placeholder: "团队报名填写；个人报名可留空",
      },
      {
        key: "track",
        label: "关注赛道",
        type: "select",
        required: true,
        options: ["Agent 工具", "教育实训", "内容生产", "商业应用"],
      },
      {
        key: "problem",
        label: "你想解决什么问题？",
        type: "textarea",
        required: true,
        placeholder: "简单描述你在意的问题、用户和想做的方向",
      },
      {
        key: "teammates",
        label: "已有队友或希望补充的角色",
        type: "textarea",
        required: false,
        placeholder: "例如：已有后端同学，希望现场找设计 / 前端",
      },
    ],
    submissionRequirements: [
      "项目标题与一句话简介",
      "Demo 链接或可运行 HTML",
      "代码仓库或说明文档",
      "PPT、HTML、MP3、图片等轻量附件",
      "不接收视频上传",
    ],
  },
  {
    id: "ignai-campus-agent-lab",
    title: "Campus Agent Lab · 实训专场",
    status: "preview",
    statusText: "筹备中",
    date: "2026.08",
    location: "长沙 · 高校实训",
    cover: "./assets/img/ignai-business-card-mockup.webp",
    summary: "围绕高校软件系统实训，把课程任务、作品提交和复盘沉淀变成 Agent 工作流。",
    href: "./event.html?id=ignai-campus-agent-lab",
    projectsHref: "./projects.html?event=ignai-campus-agent-lab",
    keywords: ["Campus", "Agent", "Java", "Practice"],
    tracks: [
      { name: "教育实训", description: "课程作业、项目管理、作品提交和学习反馈。" },
      { name: "Agent 工具", description: "面向老师和学生的任务拆解与资料整理 Agent。" },
    ],
    stages: [
      { key: "preview", label: "筹备中", done: true },
      { key: "registration", label: "报名开放", done: false },
      { key: "submission", label: "作品提交", done: false },
      { key: "showcase", label: "公开展示", done: false },
    ],
    formFields: [
      { key: "name", label: "姓名", type: "text", required: true, placeholder: "请填写姓名" },
      { key: "school", label: "学校 / 班级", type: "text", required: true, placeholder: "例如：长理软件 2301" },
      {
        key: "track",
        label: "关注方向",
        type: "select",
        required: true,
        options: ["教育实训", "Agent 工具"],
      },
      {
        key: "idea",
        label: "你想做的实训工具",
        type: "textarea",
        required: true,
        placeholder: "描述一个你想在实训里解决的问题",
      },
    ],
    submissionRequirements: ["项目说明", "演示链接", "PPT 或 HTML", "图片或文档附件", "不接收视频上传"],
  },
];

const hackathonProjects = [
  {
    id: "campus-agent-hub",
    eventId: "ignai-ai-skillathon",
    title: "Campus Agent Hub",
    track: "Agent 工具",
    image: "./assets/img/local-global-embers.png",
    tagline: "面向高校实训的课程 Agent 与作品沉淀平台。",
    description: "把任务拆解、提交提醒、作品归档和复盘摘要集中到一个学生能持续使用的工作台。",
    files: ["PPT", "HTML", "图片"],
    votes: 128,
    score: 4.7,
    featured: true,
    submittedAt: "2026-07-21T21:20:00+08:00",
  },
  {
    id: "feishu-sync-board",
    eventId: "ignai-ai-skillathon",
    title: "Feishu Sync Board",
    track: "教育实训",
    image: "./assets/img/ignai-business-card-mockup.webp",
    tagline: "把报名、作品和附件资料归档到飞书协作面板。",
    description: "管理员在平台审核，运营同学在飞书看板协作，资料双端同步但主数据留在平台。",
    files: ["PPT", "图片", "文档"],
    votes: 96,
    score: 4.5,
    featured: false,
    submittedAt: "2026-07-21T20:34:00+08:00",
  },
  {
    id: "review-draft-agent",
    eventId: "ignai-ai-skillathon",
    title: "Review Draft Agent",
    track: "内容生产",
    image: "./assets/img/halo-showcase.png",
    tagline: "把现场复盘整理成邮件、图文和活动记录草稿。",
    description: "输入现场笔记、音频和作品摘要，自动生成可编辑的通知邮件和复盘内容。",
    files: ["MP3", "文档", "图片"],
    votes: 74,
    score: 4.4,
    featured: true,
    submittedAt: "2026-07-21T22:12:00+08:00",
  },
  {
    id: "local-signal-map",
    eventId: "ignai-ai-skillathon",
    title: "Local Signal Map",
    track: "商业应用",
    image: "./assets/img/collaboration-threads.png",
    tagline: "帮本地小团队发现可以立刻验证的 AI 应用机会。",
    description: "把用户访谈、行业案例和本地资源整理成机会地图，再输出一页式验证方案。",
    files: ["HTML", "图片", "文档"],
    votes: 61,
    score: 4.2,
    featured: false,
    submittedAt: "2026-07-21T19:50:00+08:00",
  },
  {
    id: "practice-agent-board",
    eventId: "ignai-campus-agent-lab",
    title: "Practice Agent Board",
    track: "教育实训",
    image: "./assets/img/collaboration-threads.png",
    tagline: "面向实训课程的任务拆解和作品提交看板。",
    description: "老师配置任务，学生提交阶段成果，Agent 自动整理进度和问题。",
    files: ["PPT", "HTML"],
    votes: 32,
    score: 4.1,
    featured: true,
    submittedAt: "2026-08-12T18:30:00+08:00",
  },
];

function escapeEventHtml(value) {
  return String(value ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

function eventQueryValue(name, fallback) {
  const params = new URLSearchParams(window.location.search);
  return params.get(name) || fallback;
}

function findEvent(id) {
  return hackathonEvents.find((event) => event.id === id) || hackathonEvents[0];
}

function projectsForEvent(eventId) {
  return hackathonProjects.filter((project) => project.eventId === eventId);
}

function setText(id, value) {
  const target = document.getElementById(id);
  if (target) target.textContent = value;
}

function setHref(id, value) {
  const target = document.getElementById(id);
  if (target) target.href = value;
}

function renderHomeEvents() {
  const list = document.getElementById("event-list");
  if (!list) return;

  list.innerHTML = hackathonEvents
    .map(
      (event, index) => `
        <article class="event-card" data-reveal data-reveal-delay="${Math.min(index + 1, 4)}">
          <img src="${escapeEventHtml(event.cover)}" alt="${escapeEventHtml(event.title)} 主视觉" />
          <div class="event-card-body">
            <div class="event-card-meta">
              <span>${escapeEventHtml(event.statusText)}</span>
              <span>${escapeEventHtml(event.location)}</span>
            </div>
            <h3>${escapeEventHtml(event.title)}</h3>
            <p>${escapeEventHtml(event.summary)}</p>
            <dl>
              <div><dt>时间</dt><dd>${escapeEventHtml(event.date)}</dd></div>
              <div><dt>作品</dt><dd>${projectsForEvent(event.id).length} 个 mock 项目</dd></div>
            </dl>
            <div class="event-card-actions">
              <a class="primary-button" href="${escapeEventHtml(event.href)}">进入活动</a>
              <a class="outline-button" href="${escapeEventHtml(event.projectsHref)}">作品平台</a>
            </div>
          </div>
        </article>
      `,
    )
    .join("");
}

function renderStatusRail(event) {
  const rail = document.getElementById("event-status-rail");
  if (!rail) return;

  rail.innerHTML = event.stages
    .map(
      (stage) => `
        <div class="${stage.done ? "done" : stage.key === event.status ? "active" : ""}">
          <span>${escapeEventHtml(stage.label)}</span>
        </div>
      `,
    )
    .join("");
}

function renderTracks(event) {
  const grid = document.getElementById("event-track-grid");
  if (!grid) return;

  grid.innerHTML = event.tracks
    .map(
      (track, index) => `
        <article>
          <span>${String(index + 1).padStart(2, "0")}</span>
          <h3>${escapeEventHtml(track.name)}</h3>
          <p>${escapeEventHtml(track.description)}</p>
        </article>
      `,
    )
    .join("");
}

function renderEventInput(field) {
  const required = field.required ? "required" : "";
  const placeholder = escapeEventHtml(field.placeholder || "参赛者填写内容");
  const name = escapeEventHtml(field.key);

  if (field.type === "textarea") {
    return `<textarea name="${name}" rows="4" placeholder="${placeholder}" ${required}></textarea>`;
  }

  if (field.type === "select") {
    return `
      <select name="${name}" ${required}>
        <option value="">请选择</option>
        ${(field.options || [])
          .map((option) => `<option value="${escapeEventHtml(option)}">${escapeEventHtml(option)}</option>`)
          .join("")}
      </select>
    `;
  }

  if (field.type === "radio" || field.type === "checkbox") {
    return `
      <div class="choice-group">
        ${(field.options || [])
          .map(
            (option, index) => `
              <label>
                <input
                  type="${field.type}"
                  name="${name}"
                  value="${escapeEventHtml(option)}"
                  ${field.type === "radio" && index === 0 ? "checked" : ""}
                  ${required}
                />
                <span>${escapeEventHtml(option)}</span>
              </label>
            `,
          )
          .join("")}
      </div>
    `;
  }

  return `<input type="${field.type === "url" ? "url" : "text"}" name="${name}" placeholder="${placeholder}" ${required} />`;
}

function renderRegistrationForm(event) {
  const form = document.getElementById("event-registration-form");
  if (!form) return;

  form.innerHTML = `
    <div class="event-form-head">
      <span>Mock Registration</span>
      <strong>${escapeEventHtml(event.title)}</strong>
      <p>提交后会暂存在当前浏览器，用来验证完整报名链路。</p>
    </div>
    ${event.formFields
      .map(
        (field) => `
          <label class="event-question">
            <strong>${escapeEventHtml(field.label)}${field.required ? " *" : ""}</strong>
            ${renderEventInput(field)}
          </label>
        `,
      )
      .join("")}
    <div class="event-form-actions">
      <button class="primary-button" id="event-submit-button" type="submit">提交报名</button>
      <a class="outline-button" href="${escapeEventHtml(event.projectsHref)}">先看作品平台</a>
      <p id="event-form-feedback" role="status">报名将记录到本地 mock 数据，后端接入后可同步飞书。</p>
    </div>
  `;
}

function bindRegistrationForm(event) {
  const form = document.getElementById("event-registration-form");
  if (!form) return;

  form.addEventListener("submit", (submitEvent) => {
    submitEvent.preventDefault();
    const data = new FormData(form);
    const payload = {
      eventId: event.id,
      submittedAt: new Date().toISOString(),
      answers: {},
    };

    event.formFields.forEach((field) => {
      const value = field.type === "checkbox" ? data.getAll(field.key) : data.get(field.key);
      payload.answers[field.key] = value;
    });

    localStorage.setItem(`${IGN_EVENT_REGISTRATION_KEY}:${event.id}`, JSON.stringify(payload));
    const feedback = document.getElementById("event-form-feedback");
    const button = document.getElementById("event-submit-button");
    if (feedback) {
      feedback.textContent = "报名信息已写入 mock 数据，后台可进入报名管理查看同类结构。";
      feedback.classList.add("success");
    }
    if (button) button.textContent = "已记录报名";
  });
}

function renderSubmission(event) {
  const grid = document.getElementById("event-submission-grid");
  if (!grid) return;

  grid.innerHTML = event.submissionRequirements
    .map(
      (item, index) => `
        <article data-reveal data-reveal-delay="${Math.min(index + 1, 4)}">
          <span>${String(index + 1).padStart(2, "0")}</span>
          <h3>${escapeEventHtml(item)}</h3>
        </article>
      `,
    )
    .join("");
}

function renderEventPage() {
  const root = document.getElementById("event-detail-root");
  if (!root) return;

  const event = findEvent(eventQueryValue("id", "ignai-ai-skillathon"));
  document.title = `${event.title} · 活动详情`;
  setText("event-status-line", `${event.statusText} · ${event.location}`);
  setText("event-title", event.title);
  setText("event-summary", event.summary);
  setText("event-date", event.date);
  setText("event-location", event.location);
  setText("event-state", event.statusText);
  setHref("event-project-nav", event.projectsHref);
  setHref("event-project-header-link", event.projectsHref);
  setHref("event-project-hero-link", event.projectsHref);
  setHref("event-footer-project-link", event.projectsHref);

  const cover = document.getElementById("event-cover");
  if (cover) {
    cover.src = event.cover;
    cover.alt = `${event.title} 主视觉`;
  }

  const keywords = document.getElementById("event-keywords");
  if (keywords) {
    keywords.innerHTML = event.keywords.map((keyword) => `<span>${escapeEventHtml(keyword)}</span>`).join("");
  }

  renderStatusRail(event);
  renderTracks(event);
  renderRegistrationForm(event);
  renderSubmission(event);
  bindRegistrationForm(event);
}

function getLikedProjects() {
  try {
    return new Set(JSON.parse(localStorage.getItem(IGN_PROJECT_LIKES_KEY) || "[]"));
  } catch {
    return new Set();
  }
}

function saveLikedProjects(liked) {
  localStorage.setItem(IGN_PROJECT_LIKES_KEY, JSON.stringify([...liked]));
}

function sortedProjects(projects, sort) {
  const list = [...projects];
  if (sort === "votes") return list.sort((a, b) => b.votes - a.votes);
  if (sort === "latest") return list.sort((a, b) => new Date(b.submittedAt) - new Date(a.submittedAt));
  return list.sort((a, b) => Number(b.featured) - Number(a.featured) || b.votes - a.votes);
}

function renderProjectFilters(event, projects) {
  const filters = document.getElementById("project-track-filters");
  if (!filters) return;

  const tracks = ["全部", ...new Set([...event.tracks.map((track) => track.name), ...projects.map((project) => project.track)])];
  filters.innerHTML = tracks
    .map(
      (track, index) => `
        <button class="${index === 0 ? "active" : ""}" type="button" data-project-filter="${escapeEventHtml(track)}">
          ${escapeEventHtml(track)}
        </button>
      `,
    )
    .join("");
}

function renderProjectCards(projects, filter = "全部", sort = "featured") {
  const gallery = document.getElementById("project-gallery");
  if (!gallery) return;

  const liked = getLikedProjects();
  const filtered = projects.filter((project) => filter === "全部" || project.track === filter);
  const list = sortedProjects(filtered, sort);

  gallery.innerHTML = list.length
    ? list
        .map((project) => {
          const isLiked = liked.has(project.id);
          const votes = project.votes + (isLiked ? 1 : 0);
          return `
            <article class="project-showcase-card" data-project-id="${escapeEventHtml(project.id)}">
              <img src="${escapeEventHtml(project.image)}" alt="${escapeEventHtml(project.title)} 作品封面" />
              <div class="project-showcase-body">
                <div class="event-card-meta">
                  <span>${escapeEventHtml(project.track)}</span>
                  <span>${project.featured ? "精选" : "公开"}</span>
                </div>
                <h3>${escapeEventHtml(project.title)}</h3>
                <p>${escapeEventHtml(project.tagline)}</p>
                <p>${escapeEventHtml(project.description)}</p>
                <div class="file-tags">
                  ${project.files.map((file) => `<span>${escapeEventHtml(file)}</span>`).join("")}
                </div>
                <footer>
                  <strong>${votes} likes</strong>
                  <small>${project.score} score</small>
                  <button class="like-button ${isLiked ? "liked" : ""}" type="button" data-like-project="${escapeEventHtml(project.id)}">
                    ${isLiked ? "已点赞" : "点赞"}
                  </button>
                </footer>
              </div>
            </article>
          `;
        })
        .join("")
    : `<p class="empty-state">当前筛选下还没有公开作品。</p>`;
}

function bindProjectPlatform(projects) {
  let activeFilter = "全部";
  const sortSelect = document.getElementById("project-sort-select");
  const filters = document.getElementById("project-track-filters");
  const gallery = document.getElementById("project-gallery");

  filters?.addEventListener("click", (event) => {
    const button = event.target.closest("[data-project-filter]");
    if (!button) return;
    activeFilter = button.dataset.projectFilter;
    filters.querySelectorAll("button").forEach((item) => item.classList.toggle("active", item === button));
    renderProjectCards(projects, activeFilter, sortSelect?.value || "featured");
  });

  sortSelect?.addEventListener("change", () => {
    renderProjectCards(projects, activeFilter, sortSelect.value);
  });

  gallery?.addEventListener("click", (event) => {
    const button = event.target.closest("[data-like-project]");
    if (!button) return;
    const liked = getLikedProjects();
    liked.add(button.dataset.likeProject);
    saveLikedProjects(liked);
    renderProjectCards(projects, activeFilter, sortSelect?.value || "featured");
  });
}

function renderProjectsPage() {
  const root = document.getElementById("project-platform-root");
  if (!root) return;

  const event = findEvent(eventQueryValue("event", "ignai-ai-skillathon"));
  const projects = projectsForEvent(event.id);
  document.title = `${event.title} · 作品平台`;
  setText("projects-title", `${event.title} 作品平台`);
  setText("projects-summary", `${event.statusText} · ${event.location} · ${event.date}。作品按赛道、票数和精选状态展示，MVP 不展示视频。`);
  setText("projects-count-label", `${projects.length} 个作品`);
  setText("project-toolbar-title", `${event.title} · 作品列表`);
  setHref("projects-event-nav", event.href);
  setHref("projects-event-header-link", event.href);
  setHref("projects-footer-event-link", event.href);

  renderProjectFilters(event, projects);
  renderProjectCards(projects);
  bindProjectPlatform(projects);
}

document.addEventListener("DOMContentLoaded", () => {
  renderHomeEvents();
  renderEventPage();
  renderProjectsPage();
});
