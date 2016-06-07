/*global define, $, console, window, history, tau, document*/

/**
 * Settings page module
 */

define({
    name: 'views/page/settings',
    requires: [
        'core/event',
        'models/settings'
    ],
    def: function viewsPageSettings(req) {
        'use strict';

        var e = req.core.event,
            settings = req.models.settings,
            page = null,
            elUnit = null;

        function show() {
            tau.changePage('#settings');
        }

        function setElementUnit() {
            elUnit.innerHTML = (settings.getUnit());
        }

        function onSettingsReady() {
            setElementUnit();
        }

        function onPageShow() {
            setElementUnit();
        }

        function bindEvents() {
            page.addEventListener('pageshow', onPageShow);
        }

        function init() {
            page = document.getElementById('settings');
            elUnit = page.querySelector('.settings-units-value');
            bindEvents();
        }

        e.on({
            'views.page.main.settings.show': show,
            'views.page.units.settings.show': show,
            'models.settings.ready': onSettingsReady
        });

        return {
            init: init
        };
    }

});
