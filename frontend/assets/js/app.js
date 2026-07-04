const modes = {
  signup: {
    title: "报名字段配置",
    html: `
      <div class="field-list">
        <div class="data-row"><strong>姓名 / 联系方式</strong><span>文本字段，必填，作为去重主键</span><small class="mini-tag">required</small></div>
        <div class="data-row"><strong>报名类型</strong><span>个人报名 / 团队报名，影响后续队员字段</span><small class="mini-tag">radio</small></div>
        <div class="data-row"><strong>技能标签</strong><span>产品、设计、前端、后端、Agent、内容、运营</span><small class="mini-tag">multi</small></div>
        <div class="data-row"><strong>赛道意向</strong><span>AI 工具、校园实训、内容生产、商业应用</span><small class="mini-tag">select</small></div>
      </div>
    `,
  },
  review: {
    title: "AI 整理与人工审核",
    html: `
      <div class="review-list">
        <div class="data-row"><strong>林同学</strong><span>AI 摘要：前端 + 交互，寻找后端队友</span><small class="mini-tag">待审核</small></div>
        <div class="data-row"><strong>火种小队</strong><span>AI 标签：Agent / 飞书 / 教育工具</span><small class="mini-tag">通过</small></div>
        <div class="data-row"><strong>未命名团队</strong><span>队员手机号重复，需要管理员确认</span><small class="mini-tag">冲突</small></div>
        <div class="data-row"><strong>邮件 Agent</strong><span>已生成通过通知草稿，等待编辑确认</span><small class="mini-tag">draft</small></div>
      </div>
    `,
  },
  project: {
    title: "项目提交管理",
    html: `
      <div class="submission-list">
        <div class="data-row"><strong>Campus Agent Hub</strong><span>PPT、Demo 链接、GitHub 链接、本地附件备份完成</span><small class="mini-tag">已提交</small></div>
        <div class="data-row"><strong>Feishu Sync Board</strong><span>HTML 原型与 MP3 说明已上传，等待飞书云盘备份</span><small class="mini-tag">待同步</small></div>
        <div class="data-row"><strong>Prompt Studio</strong><span>缺少队伍成员确认，提交暂存</span><small class="mini-tag">草稿</small></div>
      </div>
    `,
  },
  rating: {
    title: "开放评分规则",
    html: `
      <div class="rating-box">
        <div class="score-result"><span>当前作品均分</span><br><strong id="score-value">4.6</strong><span> / 5 · 128 人参与</span></div>
        <div class="score-buttons" aria-label="评分按钮">
          <button type="button" data-score="1">1</button>
          <button type="button" data-score="2">2</button>
          <button type="button" data-score="3">3</button>
          <button type="button" data-score="4">4</button>
          <button type="button" data-score="5" class="active">5</button>
        </div>
        <div class="data-row"><strong>防重复</strong><span>按登录用户、手机号、现场口令或浏览器指纹组合限制</span><small class="mini-tag">rule</small></div>
      </div>
    `,
  },
  sync: {
    title: "飞书备份队列",
    html: `
      <div class="sync-list">
        <div class="data-row"><strong>报名 Base</strong><span>86 条报名记录已同步，2 条等待重试</span><small class="mini-tag">Base</small></div>
        <div class="data-row"><strong>作品资料</strong><span>附件 metadata 已同步，原文件写入本地备份目录</span><small class="mini-tag">Drive</small></div>
        <div class="data-row"><strong>看板视图</strong><span>待审核、已通过、已提交、已归档四个泳道</span><small class="mini-tag">Board</small></div>
      </div>
    `,
  },
};

const panel = document.querySelector("#dynamic-panel");
const title = document.querySelector("#mode-title");
const buttons = document.querySelectorAll(".mode-button");
const feed = document.querySelector("#feed-list");
const syncButton = document.querySelector("#sync-button");

function addFeed(label, message) {
  const item = document.createElement("li");
  item.innerHTML = `<strong>${label}</strong><span>${message}</span>`;
  feed.prepend(item);
  while (feed.children.length > 5) {
    feed.removeChild(feed.lastElementChild);
  }
}

function bindRating() {
  document.querySelectorAll("[data-score]").forEach((scoreButton) => {
    scoreButton.addEventListener("click", () => {
      document.querySelectorAll("[data-score]").forEach((button) => button.classList.remove("active"));
      scoreButton.classList.add("active");
      const score = scoreButton.dataset.score;
      document.querySelector("#score-value").textContent = `${Number(score).toFixed(1)}`;
      addFeed("开放评分", `当前样例作品已记录 ${score} 分评分`);
    });
  });
}

function setMode(mode) {
  const config = modes[mode];
  title.textContent = config.title;
  panel.innerHTML = config.html;
  buttons.forEach((button) => {
    button.classList.toggle("active", button.dataset.mode === mode);
  });
  if (mode === "rating") bindRating();
}

buttons.forEach((button) => {
  button.addEventListener("click", () => {
    setMode(button.dataset.mode);
  });
});

syncButton.addEventListener("click", () => {
  addFeed("飞书同步", "已创建同步任务：报名、作品、附件 metadata");
});

setMode("signup");
