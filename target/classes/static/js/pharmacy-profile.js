(function () {
    'use strict';

    /* Animated stat counters */
    var counters = document.querySelectorAll('.stat-num[data-count]');
    if (!counters.length) return;

    var animated = new WeakSet();

    function animateCounter(el) {
        if (animated.has(el)) return;
        animated.add(el);

        var target = parseInt(el.getAttribute('data-count'), 10);
        var suffix = el.getAttribute('data-suffix') || '';
        if (isNaN(target)) return;

        var duration = 1800;
        var start = 0;
        var startTime = null;

        function step(timestamp) {
            if (!startTime) startTime = timestamp;
            var progress = Math.min((timestamp - startTime) / duration, 1);
            var eased = 1 - Math.pow(1 - progress, 3);
            var current = Math.floor(start + (target - start) * eased);
            el.textContent = current.toLocaleString('en-IN') + suffix;
            if (progress < 1) {
                requestAnimationFrame(step);
            } else {
                el.textContent = target.toLocaleString('en-IN') + suffix;
            }
        }

        requestAnimationFrame(step);
    }

    if ('IntersectionObserver' in window) {
        var counterObserver = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    animateCounter(entry.target);
                }
            });
        }, { threshold: 0.4 });

        counters.forEach(function (el) {
            counterObserver.observe(el);
        });
    } else {
        counters.forEach(animateCounter);
    }

    /* Skill bar animation on scroll */
    var skillBars = document.querySelectorAll('.skill-bar-fill[data-skill-pct]');
    if (!skillBars.length) return;

    var barsAnimated = new WeakSet();

    function animateSkillBar(bar) {
        if (barsAnimated.has(bar)) return;
        barsAnimated.add(bar);
        var pct = bar.getAttribute('data-skill-pct');
        bar.style.width = pct + '%';
    }

    if ('IntersectionObserver' in window) {
        var skillObserver = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    animateSkillBar(entry.target);
                }
            });
        }, { threshold: 0.3 });

        skillBars.forEach(function (bar) {
            skillObserver.observe(bar);
        });
    } else {
        skillBars.forEach(animateSkillBar);
    }
})();
