/*global define, $, console, window, document, history*/

/**
 * Units page module
 */

define({
    name: 'views/page/units',
    requires: [
        'core/event',
        'models/settings'
    ],
    def: function viewsPageUnits(req) {
        'use strict';

        var e = req.core.event,
            settings = req.models.settings,
            page = null,
            okButton = null,
            cancelButton = null,
            radio = null;

        function onSave() {
            var unit = page.querySelector('input[type="radio"]:checked').value;
            settings.setUnit(unit);
            e.fire('settings.show');
        }

        function onCancel() {
            history.back();
        }

        function checkDefault() {
            radio = page.querySelector(
                'input[value="' + settings.getUnit() + '"]'
            );
            radio.checked = true;
        }

        function onPageShow() {
            checkDefault();
        }

        function bindEvents() {
            page.addEventListener('pageshow', onPageShow);
            okButton.addEventListener('click', onSave);
            cancelButton.addEventListener('click', onCancel);
        }

        function init() {
            page = document.getElementById('units');
            okButton = page.getElementsByClassName('ok')[0];
            cancelButton = page.getElementsByClassName('cancel')[0];
            bindEvents();
        }

        return {
            init: init
        };
    }

});
