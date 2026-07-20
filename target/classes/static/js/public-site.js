(function () {
    'use strict';

    /* Navbar scroll effect */
    var navbar = document.querySelector('.public-navbar');
    if (navbar) {
        window.addEventListener('scroll', function () {
            navbar.classList.toggle('scrolled', window.scrollY > 40);
        }, { passive: true });
    }

    /* Intersection Observer — replay reveal on every scroll into view */
    var revealEls = document.querySelectorAll('.reveal');

    function setRevealVisible(el, show) {
        if (show) {
            el.classList.add('visible');
        } else {
            el.classList.remove('visible');
        }
    }

    function refreshVisibleReveals() {
        if (!revealEls.length) return;
        var viewHeight = window.innerHeight || document.documentElement.clientHeight;
        revealEls.forEach(function (el) {
            var rect = el.getBoundingClientRect();
            var inView = rect.top < viewHeight * 0.88 && rect.bottom > viewHeight * 0.08;
            setRevealVisible(el, inView);
        });
    }

    if (revealEls.length && 'IntersectionObserver' in window) {
        var observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                setRevealVisible(entry.target, entry.isIntersecting);
            });
        }, {
            threshold: [0, 0.12, 0.25],
            rootMargin: '0px 0px -8% 0px'
        });

        revealEls.forEach(function (el) {
            observer.observe(el);
        });

        /* Replay animations on full page load and browser back/forward */
        window.addEventListener('pageshow', function () {
            revealEls.forEach(function (el) {
                el.classList.remove('visible');
            });
            requestAnimationFrame(refreshVisibleReveals);
        });

        requestAnimationFrame(refreshVisibleReveals);
    } else if (revealEls.length) {
        revealEls.forEach(function (el) {
            el.classList.add('visible');
        });
    }

    /* Smooth scroll for anchor links */
    document.querySelectorAll('a[href^="#"]').forEach(function (anchor) {
        anchor.addEventListener('click', function (e) {
            var targetId = this.getAttribute('href');
            if (targetId.length <= 1) return;
            var target = document.querySelector(targetId);
            if (target) {
                e.preventDefault();
                var offset = navbar ? navbar.offsetHeight + 12 : 0;
                var top = target.getBoundingClientRect().top + window.scrollY - offset;
                window.scrollTo({ top: top, behavior: 'smooth' });
            }
        });
    });

    /* Close mobile nav on link click */
    var navCollapse = document.getElementById('publicNav');
    if (navCollapse) {
        navCollapse.querySelectorAll('.nav-link').forEach(function (link) {
            link.addEventListener('click', function () {
                if (navCollapse.classList.contains('show')) {
                    var toggler = document.querySelector('.navbar-toggler');
                    if (toggler) toggler.click();
                }
            });
        });
    }
})();
