const ribbons = document.querySelector(".marquee div");

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
