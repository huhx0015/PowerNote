/*global define, $, console, window, history, document*/

/**
 * Main page module
 */

define({
    name: 'views/page/main',
    requires: [
        'core/event',
        'core/application',
        'views/page/history',
        'views/page/settings',
        'views/page/register'
    ],
    def: function viewsPageMain(req) {
        'use strict';

        var e = req.core.event,
            app = req.core.application,
            page = null;

        function onPageShow() {}

        function onHistoryBtnClick() {
            e.fire('history.show');
        }

        function onExitBtnClick() {
            app.exit();
        }

        function onSettingsBtnClick() {
            e.fire('settings.show');
        }

        function onRegisterBtnClick() {
            e.fire('register.show');
        }

        function bindEvents() {
            var historyBtnEl = document.getElementById('history-btn'),
                exitBtnEl = document.getElementById('exit-btn'),
                settingsBtnEl = document.getElementById('settings-btn'),
                registerBtnEl = document.getElementById('register-btn');

            page.addEventListener('pageshow', onPageShow);
            historyBtnEl.addEventListener('click', onHistoryBtnClick);
            exitBtnEl.addEventListener('click', onExitBtnClick);
            settingsBtnEl.addEventListener('click', onSettingsBtnClick);
            registerBtnEl.addEventListener('click', onRegisterBtnClick);
        }

        function init() {
            page = document.getElementById('main');
            bindEvents();
        }

        return {
            init: init
        };
    }

});
