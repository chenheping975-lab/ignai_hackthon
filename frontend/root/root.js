const state = {
  rootEmail: localStorage.getItem("ignai_root_owner_email") || "",
  selectedRegistrationId: 1,
  previewRole: "guest",
  previewPhase: "registration",
  fields: [
    {
      id: "name",
      label: "姓名",
      type: "text",
      required: true,
      placeholder: "请填写真实姓名",
      options: [],
    },
    {
      id: "phone",
      label: "手机号",
      type: "text",
      required: true,
      placeholder: "用于接收活动通知",
      options: [],
    },
    {
      id: "track",
      label: "关注赛道",
      type: "select",
      required: true,
      placeholder: "请选择赛道",
      options: ["Agent 工具", "教育实训", "内容生产", "商业应用"],
    },
    {
      id: "problem",
      label: "你想解决什么问题？",
      type: "textarea",
      required: true,
      placeholder: "简单描述你在意的问题和想做的方向",
      options: [],
    },
  ],
  registrations: [
    {
      id: 1,
      name: "陈晨",
      team: "Torch Lab",
      type: "团队报名",
      track: "Agent 工具",
      status: "pending",
      risk: "low",
      tags: ["Agent", "教育实训", "全栈"],
      aiDone: false,
      summary: "团队希望做一个面向软件实训课程的 Agent 工具，帮助学生整理任务和提交作品。",
      answers: ["有 Java / 前端基础", "希望做课程实训 Agent", "需要现场找一名设计同学"],
      email: "chenchen@example.com",
    },
    {
      id: 2,
      name: "李想",
      team: "个人报名",
      type: "个人报名",
      track: "内容生产",
      status: "pending",
      risk: "medium",
      tags: ["内容", "自动化"],
      aiDone: false,
      summary: "报名者想做一个把活动复盘自动整理成公众号草稿的工具。",
      answers: ["擅长写作和剪辑", "希望现场组队找工程同学"],
      email: "lixiang@example.com",
    },
    {
      id: 3,
      name: "周宁",
      team: "Feishu Sync Board",
      type: "团队报名",
      track: "教育实训",
      status: "approved",
      risk: "low",
      tags: ["飞书", "看板", "协作"],
      aiDone: true,
      summary: "团队目标清晰，准备做报名和作品资料同步到飞书看板的协作工具。",
      answers: ["已有 Demo 草图", "需要补充后端接口"],
      email: "zhouning@example.com",
    },
    {
      id: 4,
      name: "匿名用户",
      team: "乱填测试",
      type: "个人报名",
      track: "Agent 工具",
      status: "pending",
      risk: "high",
      tags: ["待核验"],
      aiDone: true,
      summary: "回答高度重复且缺少有效项目意图，建议人工核验后再处理。",
      answers: ["随便", "不知道", "111"],
      email: "unknown@example.com",
    },
    {
      id: 5,
      name: "苏叶",
      team: "Voice Zine Lab",
      type: "团队报名",
      track: "内容生产",
      status: "pending",
      risk: "low",
      tags: ["播客", "Newsletter", "AI 写作"],
      aiDone: false,
      summary: "团队想做一个把现场录音转成长文和播客脚本的 Agent，已有早期 Demo。",
      answers: ["已有 5 期播客素材", "希望现场补一名前端"],
      email: "suye@example.com",
    },
    {
      id: 6,
      name: "高远",
      team: "Topic Radar",
      type: "个人报名",
      track: "Agent 工具",
      status: "waitlist",
      risk: "medium",
      tags: ["RSS", "选题", "Agent"],
      aiDone: true,
      summary: "报名者希望做内容创作者的话题雷达，技术基础扎实但赛道匹配度需现场确认。",
      answers: ["熟悉 RSS / 社媒 API", "想用 Agent 整理每周选题"],
      email: "gaoyuan@example.com",
    },
  ],
  projects: [
    {
      id: 1,
      title: "Campus Agent Hub",
      track: "Agent 工具",
      status: "submitted",
      votes: 128,
      score: 4.7,
      featured: true,
      files: ["PPT", "HTML", "图片"],
      description: "面向高校实训的课程 Agent 与作品沉淀平台。",
    },
    {
      id: 2,
      title: "Feishu Sync Board",
      track: "教育实训",
      status: "published",
      votes: 96,
      score: 4.5,
      featured: false,
      files: ["PPT", "图片"],
      description: "把报名、作品和附件资料归档到飞书协作面板。",
    },
    {
      id: 3,
      title: "Review Draft Agent",
      track: "内容生产",
      status: "locked",
      votes: 42,
      score: 4.2,
      featured: false,
      files: ["MP3", "文档", "图片"],
      description: "把现场复盘自动整理成邮件和公众号草稿。",
    },
    {
      id: 4,
      title: "Voice Zine Agent",
      track: "内容生产",
      status: "published",
      votes: 156,
      score: 4.8,
      featured: true,
      files: ["MP3", "文档", "图片"],
      description: "现场录音和复盘音频自动转成可发布的长文和播客脚本。",
    },
    {
      id: 5,
      title: "Topic Radar",
      track: "Agent 工具",
      status: "published",
      votes: 102,
      score: 4.5,
      featured: false,
      files: ["HTML", "图片"],
      description: "聚合多个社媒和 RSS 信号，给内容创作者生成每周选题。",
    },
  ],
};

const statusText = {
  pending: "待审核",
  approved: "已通过",
  rejected: "已拒绝",
  waitlist: "候补",
  submitted: "已提交",
  published: "已公开",
  locked: "已锁定",
};

const riskText = {
  low: "低",
  medium: "中",
  high: "高",
};

const fieldTypes = [
  ["text", "短文本"],
  ["textarea", "长文本"],
  ["select", "下拉选择"],
  ["radio", "单选"],
  ["checkbox", "多选"],
  ["url", "链接"],
  ["file", "附件"],
];

const optionFieldTypes = new Set(["select", "radio", "checkbox"]);

function $(selector) {
  return document.querySelector(selector);
}

function $all(selector) {
  return [...document.querySelectorAll(selector)];
}

function escapeHtml(value) {
  return String(value ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

function showToast(message) {
  const toast = $("#toast");
  toast.textContent = message;
  toast.classList.add("show");
  clearTimeout(showToast.timer);
  showToast.timer = setTimeout(() => toast.classList.remove("show"), 1900);
}

function setView(viewName) {
  $all(".admin-view").forEach((view) => view.classList.toggle("active", view.id === viewName));
  $all("[data-admin-view]").forEach((button) => {
    button.classList.toggle("active", button.dataset.adminView === viewName);
  });
  if (viewName === "preview") renderEventPreview();
}

function renderRootStatus() {
  $("#root-status").textContent = state.rootEmail ? state.rootEmail : "未初始化";
  $("#sync-chip").textContent = state.rootEmail ? "飞书同步：队列空闲" : "飞书同步：待配置";
}

function renderMetrics() {
  const registrations = state.registrations;
  const metrics = [
    ["报名", registrations.length],
    ["待审核", registrations.filter((item) => item.status === "pending").length],
    ["已通过", registrations.filter((item) => item.status === "approved").length],
    ["作品", state.projects.length],
    ["点赞", state.projects.reduce((sum, item) => sum + item.votes, 0)],
    ["AI 待处理", registrations.filter((item) => !item.aiDone).length],
  ];
  $("#metric-grid").innerHTML = metrics
    .map(
      ([label, value]) => `
        <article class="metric-card">
          <span class="caption">${label}</span>
          <strong>${value}</strong>
        </article>
      `,
    )
    .join("");
  $("#today-tasks").innerHTML = [
    "12 条报名待审核",
    "4 封邮件草稿待确认",
    "3 个项目等待公开",
    state.rootEmail ? "root 已绑定创始人邮箱" : "root 尚未初始化",
  ]
    .map((task) => `<li>${task}</li>`)
    .join("");
}

function fieldCardHtml(field, index) {
  const optionsValue = (field.options || []).join("\n");
  const hasOptions = optionFieldTypes.has(field.type);
  return `
    <div class="field-item field-editor" data-field-index="${index}">
      <span class="field-index">${String(index + 1).padStart(2, "0")}</span>
      <div class="field-editor-main">
        <div class="field-row field-row-head">
          <label class="field-title">
            <span>问题标题</span>
            <input data-field-prop="label" type="text" value="${escapeHtml(field.label)}" aria-label="问题标题" placeholder="例如：你想解决什么问题？" />
          </label>
          <label class="field-required">
            <input data-field-prop="required" type="checkbox" ${field.required ? "checked" : ""} aria-label="必填" />
            <span>必填</span>
          </label>
          <button class="field-delete" data-field-delete type="button" aria-label="删除问题">删除</button>
        </div>
        <div class="field-row field-row-config">
          <label class="field-type">
            <span>题型</span>
            <select data-field-prop="type" aria-label="问题类型">
              ${fieldTypes
                .map(
                  ([value, label]) =>
                    `<option value="${value}" ${field.type === value ? "selected" : ""}>${label}</option>`,
                )
                .join("")}
            </select>
          </label>
          <label class="field-placeholder">
            <span>占位提示</span>
            <input data-field-prop="placeholder" type="text" value="${escapeHtml(field.placeholder || "")}" aria-label="占位提示" placeholder="参赛者填写时看到的灰字" />
          </label>
        </div>
        <div class="field-options-wrap" data-field-options-wrap>
          ${
            hasOptions
              ? `<label class="option-editor">
                  <span>选项（每行一个）</span>
                  <textarea data-field-prop="options" rows="3" aria-label="问题选项">${escapeHtml(optionsValue)}</textarea>
                  <small>会同步到右侧预览，参赛者将看到这些选项。</small>
                </label>`
              : ""
          }
        </div>
      </div>
    </div>
  `;
}

function renderFields() {
  $("#field-list").innerHTML = state.fields
    .map((field, index) => fieldCardHtml(field, index))
    .join("");

  renderFormPreview();
}

function previewInput(field) {
  const label = escapeHtml(field.label);
  const placeholder = escapeHtml(field.placeholder || "参赛者填写内容");

  if (field.type === "textarea") {
    return `<textarea rows="3" placeholder="${placeholder}"></textarea>`;
  }

  if (field.type === "select") {
    const options = field.options?.length ? field.options : ["选项 A", "选项 B"];
    return `
      <select>
        ${options.map((option) => `<option>${escapeHtml(option)}</option>`).join("")}
      </select>
    `;
  }

  if (field.type === "radio" || field.type === "checkbox") {
    const options = field.options?.length ? field.options : ["选项 A", "选项 B"];
    return `
      <div class="choice-preview" aria-label="${label}">
        ${options
          .map(
            (option) => `
              <label>
                <input type="${field.type}" name="preview-${escapeHtml(field.id || field.label)}" />
                <span>${escapeHtml(option)}</span>
              </label>
            `,
          )
          .join("")}
      </div>
    `;
  }

  if (field.type === "file") {
    return `<input type="file" accept=".ppt,.pptx,.pdf,.html,.zip,.mp3,.png,.jpg,.jpeg,.webp,.doc,.docx" />`;
  }

  return `<input type="${field.type === "url" ? "url" : "text"}" placeholder="${placeholder}" />`;
}

function renderFormPreview() {
  $("#form-preview").innerHTML = state.fields
    .map((field) => {
      return `
        <label class="demo-question">
          <strong>${escapeHtml(field.label)}${field.required ? " *" : ""}</strong>
          ${previewInput(field)}
        </label>
      `;
    })
    .join("");
}

function getFilteredRegistrations() {
  const status = $("#registration-status-filter").value;
  const track = $("#registration-track-filter").value;
  const keyword = $("#registration-search").value.trim().toLowerCase();
  return state.registrations.filter((item) => {
    const matchesStatus = status === "all" || item.status === status;
    const matchesTrack = track === "all" || item.track === track;
    const haystack = `${item.name} ${item.team} ${item.track} ${item.tags.join(" ")}`.toLowerCase();
    const matchesKeyword = !keyword || haystack.includes(keyword);
    return matchesStatus && matchesTrack && matchesKeyword;
  });
}

function renderRegistrations() {
  $("#registration-table").innerHTML = getFilteredRegistrations()
    .map(
      (item) => `
        <tr data-registration-id="${item.id}">
          <td><strong>${item.team}</strong><br /><span class="field-meta">${item.name} · ${item.type}</span></td>
          <td>${item.track}</td>
          <td>${item.tags.map((tag) => `<span class="pill">${tag}</span>`).join(" ")}</td>
          <td class="risk-${item.risk}">${riskText[item.risk]}</td>
          <td><span class="status ${item.status}">${statusText[item.status]}</span></td>
        </tr>
      `,
    )
    .join("");
}

function selectedRegistration() {
  return state.registrations.find((item) => item.id === state.selectedRegistrationId) || state.registrations[0];
}

function openRegistration(id) {
  state.selectedRegistrationId = Number(id);
  renderDrawer();
  $("#registration-drawer").classList.add("open");
}

function renderDrawer() {
  const item = selectedRegistration();
  $("#drawer-content").innerHTML = `
    <span class="caption">Registration detail</span>
    <h2>${item.team}</h2>
    <p>${item.name} · ${item.track} · <span class="status ${item.status}">${statusText[item.status]}</span></p>

    <div class="drawer-section">
      <strong>问卷原文</strong>
      ${item.answers.map((answer) => `<p>${answer}</p>`).join("")}
    </div>

    <div class="drawer-section">
      <strong>AI 初筛</strong>
      <p id="ai-summary">${item.summary}</p>
      <div>${item.tags.map((tag) => `<span class="pill">${tag}</span>`).join(" ")}</div>
      <p>风险：<span class="risk-${item.risk}">${riskText[item.risk]}</span> · ${item.aiDone ? "已完成" : "待处理"}</p>
      <button class="add-button" id="run-ai-screen" type="button">运行 AI 初筛</button>
    </div>

    <div class="drawer-section">
      <strong>审核操作</strong>
      <div class="drawer-actions">
        <button class="primary" type="button" data-review-status="approved">通过并生成邮件</button>
        <button type="button" data-review-status="waitlist">候补</button>
        <button type="button" data-review-status="rejected">拒绝</button>
      </div>
    </div>
  `;
}

function generateMailDraft(item) {
  $("#mail-subject").value = `IGNAI AI Skillathon 报名结果通知 - ${item.team}`;
  $("#mail-body").value = `${item.name}，你好：

我们已经看到了你提交的「${item.team}」报名信息。你选择的方向是「${item.track}」，其中关于「${item.answers[0]}」的描述让我们很期待现场交流。

你的报名状态：${statusText[item.status]}。

如果状态为已通过，请准备好现场组队沟通和作品提交资料：标题、简介、Demo 链接、代码链接、PPT/HTML/MP3/图片等。不需要准备视频。

IGNAI AI Skillathon 运营组`;
}

function reviewRegistration(status) {
  const item = selectedRegistration();
  item.status = status;
  item.aiDone = true;
  if (status === "approved" && !item.tags.includes("优先通过")) {
    item.tags.unshift("优先通过");
  }
  generateMailDraft(item);
  renderRegistrations();
  renderDrawer();
  renderMetrics();
  showToast(`${item.team} 已更新为：${statusText[status]}，邮件草稿已生成`);
}

function runAiScreen() {
  const item = selectedRegistration();
  item.aiDone = true;
  if (item.risk === "medium") item.risk = "low";
  if (!item.tags.includes("AI 已整理")) item.tags.push("AI 已整理");
  item.summary = `${item.summary} AI 已补充：回答结构完整，可进入人工审核。`;
  renderRegistrations();
  renderDrawer();
  renderMetrics();
  showToast("AI 初筛已完成，标签和摘要已更新");
}

function filteredProjects() {
  const status = $("#project-status-filter").value;
  const sort = $("#project-sort").value;
  const list = state.projects.filter((item) => status === "all" || item.status === status);
  if (sort === "votes") return list.sort((a, b) => b.votes - a.votes);
  if (sort === "track") return list.sort((a, b) => a.track.localeCompare(b.track, "zh-CN"));
  return list.sort((a, b) => b.id - a.id);
}

function renderProjects() {
  $("#project-board").innerHTML = filteredProjects()
    .map(
      (item) => `
        <article class="project-card" data-project-id="${item.id}">
          <header>
            <div>
              <span class="caption">${item.track}</span>
              <h3>${item.title}</h3>
            </div>
            <span class="status ${item.status}">${statusText[item.status]}</span>
          </header>
          <p>${item.description}</p>
          <div class="file-tags">${item.files.map((file) => `<span>${file}</span>`).join("")}</div>
          <p>${item.votes} likes · ${item.score} score · ${item.featured ? "精选" : "普通"}</p>
          <div class="project-actions">
            <button type="button" data-project-action="publish">${item.status === "published" ? "取消公开" : "设为公开"}</button>
            <button type="button" data-project-action="feature">${item.featured ? "取消精选" : "设为精选"}</button>
          </div>
        </article>
      `,
    )
    .join("");
}

function updateProject(projectId, action) {
  const project = state.projects.find((item) => item.id === Number(projectId));
  if (!project) return;
  if (action === "publish") {
    project.status = project.status === "published" ? "submitted" : "published";
  }
  if (action === "feature") {
    project.featured = !project.featured;
  }
  renderProjects();
  renderEventPreview();
  showToast(`${project.title} 展示状态已更新`);
}

function renderEventPreview() {
  const roleMap = {
    guest: ["访客", "登录或注册后可报名。", "提交报名"],
    loggedIn: ["已登录未报名", "报名窗口已开放，先填写报名问卷。", "开始报名"],
    pending: ["已报名待审核", "报名已收到，等待管理员审核和邮件通知。", "查看报名状态"],
    approved: ["已通过参赛者", "准备现场组队和作品提交资料。", "查看提交要求"],
    leader: ["队长", "队伍已确认，请在截止前提交项目资料。", "提交作品"],
    submitted: ["已提交项目", "项目等待公开展示，展示后可被点赞和评分。", "查看作品页"],
  };
  const phaseMap = {
    registration: "报名开放",
    review: "审核中",
    submission: "提交中",
    showcase: "展示中",
  };
  const [role, next, action] = roleMap[state.previewRole];
  const phase = phaseMap[state.previewPhase];
  const published = state.projects.filter((item) => item.status === "published");
  $("#event-preview-panel").innerHTML = `
    <div class="preview-hero">
      <span class="caption">IGNAI AI Skillathon</span>
      <h2>行动创造价值，人链接人。</h2>
      <p>长沙 · 两天一夜 · 当前：${phase}</p>
    </div>
    <div class="preview-body">
      <section class="preview-step">
        <span class="caption">身份预览</span>
        <h3>${role}</h3>
        <p>${next}</p>
        <button class="primary-button" type="button">${action}</button>
      </section>
      <section class="preview-step">
        <span class="caption">作品展示</span>
        <h3>${published.length || state.projects.length} 个项目可展示</h3>
        <p>支持按赛道、票数、最新和精选排序。MVP 不展示视频。</p>
        <div class="preview-project">
          <strong>${(published[0] || state.projects[0]).title}</strong>
          <p>${(published[0] || state.projects[0]).votes} likes · ${(published[0] || state.projects[0]).track}</p>
        </div>
      </section>
    </div>
  `;
}

function updateFieldFromInput(input) {
  const item = input.closest("[data-field-index]");
  if (!item) return;
  const index = Number(item.dataset.fieldIndex);
  const field = state.fields[index];
  const prop = input.dataset.fieldProp;
  if (!field || !prop) return;

  if (prop === "required") {
    field.required = input.checked;
    renderFormPreview();
    return;
  }

  if (prop === "options") {
    field.options = input.value
      .split(/\n|,/)
      .map((option) => option.trim())
      .filter(Boolean);
    renderFormPreview();
    return;
  }

  field[prop] = input.value;

  if (prop === "type") {
    if (optionFieldTypes.has(field.type) && (!field.options || field.options.length === 0)) {
      field.options = ["选项 A", "选项 B"];
    }
    if (!optionFieldTypes.has(field.type)) {
      field.options = [];
    }
    const wrap = item.querySelector("[data-field-options-wrap]");
    if (wrap) {
      wrap.innerHTML = optionFieldTypes.has(field.type)
        ? `<label class="option-editor">
            <span>选项（每行一个）</span>
            <textarea data-field-prop="options" rows="3" aria-label="问题选项">${escapeHtml((field.options || []).join("\n"))}</textarea>
            <small>会同步到右侧预览，参赛者将看到这些选项。</small>
          </label>`
        : "";
    }
    renderFormPreview();
    return;
  }

  renderFormPreview();
}

function deleteField(button) {
  const item = button.closest("[data-field-index]");
  if (!item) return;
  if (state.fields.length <= 1) {
    showToast("至少保留一个报名问题");
    return;
  }
  const index = Number(item.dataset.fieldIndex);
  const [removed] = state.fields.splice(index, 1);
  renderFields();
  showToast(`已删除问题：${removed.label}`);
}

function bindEvents() {
  $all("[data-admin-view]").forEach((button) => {
    button.addEventListener("click", () => setView(button.dataset.adminView));
  });
  $all("[data-admin-view-shortcut]").forEach((button) => {
    button.addEventListener("click", () => setView(button.dataset.adminViewShortcut));
  });

  $("#root-setup-form").addEventListener("submit", (event) => {
    event.preventDefault();
    const email = $("#root-email").value.trim();
    if (!email) {
      showToast("请先填写创始人邮箱");
      return;
    }
    state.rootEmail = email;
    localStorage.setItem("ignai_root_owner_email", email);
    renderRootStatus();
    showToast("root 已初始化，创始人邮箱已绑定");
  });

  $("#add-field-button").addEventListener("click", () => {
    const next = state.fields.length + 1;
    state.fields.push({
      id: `custom_${Date.now()}`,
      label: `新增问题 ${next}`,
      type: "text",
      required: false,
      placeholder: "参赛者在这里填写",
      options: [],
    });
    renderFields();
    showToast("已新增一个报名问题，可编辑标题、题型和必填");
    setTimeout(() => {
      const items = $all("[data-field-index]");
      const last = items[items.length - 1];
      const titleInput = last?.querySelector('[data-field-prop="label"]');
      titleInput?.focus();
      titleInput?.select();
    }, 0);
  });

  $("#field-list").addEventListener("input", (event) => {
    if (event.target.matches("[data-field-prop]")) updateFieldFromInput(event.target);
  });
  $("#field-list").addEventListener("change", (event) => {
    if (event.target.matches("[data-field-prop]")) updateFieldFromInput(event.target);
  });
  $("#field-list").addEventListener("click", (event) => {
    const deleteButton = event.target.closest("[data-field-delete]");
    if (deleteButton) deleteField(deleteButton);
  });

  ["registration-status-filter", "registration-track-filter", "registration-search"].forEach((id) => {
    $(`#${id}`).addEventListener("input", renderRegistrations);
  });

  $("#registration-table").addEventListener("click", (event) => {
    const row = event.target.closest("[data-registration-id]");
    if (row) openRegistration(row.dataset.registrationId);
  });

  $("#registration-drawer").addEventListener("click", (event) => {
    const review = event.target.closest("[data-review-status]");
    if (review) reviewRegistration(review.dataset.reviewStatus);
    if (event.target.id === "run-ai-screen") runAiScreen();
  });
  $("#drawer-close").addEventListener("click", () => $("#registration-drawer").classList.remove("open"));
  $("#batch-ai-button").addEventListener("click", () => {
    state.registrations.forEach((item) => {
      item.aiDone = true;
      if (!item.tags.includes("AI 已整理")) item.tags.push("AI 已整理");
    });
    renderRegistrations();
    renderMetrics();
    showToast("已为全部报名生成 AI 初筛结果");
  });

  ["project-status-filter", "project-sort"].forEach((id) => {
    $(`#${id}`).addEventListener("input", renderProjects);
  });
  $("#project-board").addEventListener("click", (event) => {
    const button = event.target.closest("[data-project-action]");
    const card = event.target.closest("[data-project-id]");
    if (button && card) updateProject(card.dataset.projectId, button.dataset.projectAction);
  });

  $("#preview-role").addEventListener("change", (event) => {
    state.previewRole = event.target.value;
    renderEventPreview();
  });
  $("#preview-phase").addEventListener("change", (event) => {
    state.previewPhase = event.target.value;
    renderEventPreview();
  });

  $("#send-mail-button").addEventListener("click", () => showToast("邮件已进入发送队列（静态 MVP）"));
  $("#save-event-button").addEventListener("click", () => showToast("活动配置已保存到静态原型状态"));
}

function init() {
  renderRootStatus();
  renderMetrics();
  renderFields();
  renderRegistrations();
  renderProjects();
  renderEventPreview();
  generateMailDraft(state.registrations[2]);
  bindEvents();
}

document.addEventListener("DOMContentLoaded", init);
