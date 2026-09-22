(function () {
    "use strict";

    var storageKey = "taco-cloud-theme";
    var root = document.documentElement;

    function getSavedTheme() {
        try {
            var savedTheme = localStorage.getItem(storageKey);
            return savedTheme === "dark" || savedTheme === "light" ? savedTheme : null;
        } catch (error) {
            return null;
        }
    }

    function getPreferredTheme() {
        var savedTheme = getSavedTheme();

        if (savedTheme) {
            return savedTheme;
        }

        return window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches
            ? "dark"
            : "light";
    }

    function updateLogos(theme) {
        document.querySelectorAll("img").forEach(function (image) {
            var source = image.getAttribute("src") || "";

            if (!image.dataset.themeLightSrc && /\/TacoCloud\.png(?:\?.*)?$/.test(source)) {
                image.dataset.themeLightSrc = source;
                image.dataset.themeDarkSrc = source.replace(/TacoCloud\.png(\?.*)?$/, "TacoCloudDark.jpg$1");
            }

            if (image.dataset.themeLightSrc) {
                image.setAttribute(
                    "src",
                    theme === "dark" ? image.dataset.themeDarkSrc : image.dataset.themeLightSrc
                );
            }
        });
    }

    function setTheme(theme, persist) {
        root.dataset.theme = theme;

        if (persist) {
            try {
                localStorage.setItem(storageKey, theme);
            } catch (error) {
                // The selected theme still works when storage is unavailable.
            }
        }

        var button = document.querySelector(".theme-toggle");

        if (button) {
            var darkThemeEnabled = theme === "dark";
            button.setAttribute("aria-label", darkThemeEnabled ? "Switch to light theme" : "Switch to dark theme");
            button.setAttribute("title", darkThemeEnabled ? "Light theme" : "Dark theme");
            button.setAttribute("aria-pressed", String(darkThemeEnabled));
            button.querySelector(".theme-toggle__icon").textContent = darkThemeEnabled ? "☀" : "☾";
        }

        if (document.body) {
            updateLogos(theme);
        }
    }

    function createToggle() {
        if (document.querySelector(".theme-toggle")) {
            return;
        }

        var button = document.createElement("button");
        button.type = "button";
        button.className = "theme-toggle";
        button.innerHTML = '<span class="theme-toggle__icon" aria-hidden="true"></span>';
        button.addEventListener("click", function () {
            setTheme(root.dataset.theme === "dark" ? "light" : "dark", true);
        });

        var actions = document.querySelector([
            ".header-actions",
            ".notification-header-actions",
            ".history-header-actions",
            ".profile-header-user"
        ].join(","));

        if (actions) {
            actions.insertBefore(button, actions.firstChild);
        } else {
            var siteHeader = document.querySelector(".site-header .header-inner");

            if (siteHeader) {
                siteHeader.insertBefore(button, siteHeader.querySelector(".header-button"));
            } else {
                button.classList.add("theme-toggle--floating");
                document.body.appendChild(button);
            }
        }

        setTheme(root.dataset.theme || getPreferredTheme(), false);
    }

    setTheme(getPreferredTheme(), false);

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", createToggle);
    } else {
        createToggle();
    }
})();
