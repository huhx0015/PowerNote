/*global define, $, console, tizen, webapis*/
/*jslint regexp: true*/

/**
 * Settings model.
 */

define({
    name: 'models/settings',
    requires: [
        'core/storage/idb',
        'core/event',
        'helpers/units'
    ],
    def: function modelsSettings(req) {
        'use strict';

        var s = req.core.storage.idb,
            e = req.core.event,
            units = req.helpers.units,
            defaults = {
                unit: units.UNIT_METER
            },
            settings = null,
            STORAGE_KEY = 'settings';

         /**
         * Returns unit.
         * @return {string} unit
         */
        function getUnit() {
            if (settings === null) {
                console.error('Settings not initialized yet.');
                return null;
            }
            return settings.unit;
        }

        function saveSettings() {
            s.add(STORAGE_KEY, settings);
        }

        /**
         * Sets unit.
         * @param {string} value
         */
        function setUnit(value) {
            settings.unit = value;
            saveSettings();
        }

        /**
         * Initializes module.
         */
        function init() {
            s.get(STORAGE_KEY);
        }

        function onRead(ev) {
            if (ev.detail.key !== STORAGE_KEY) {
                return;
            }
            if (typeof ev.detail.value !== 'object') {
                settings = defaults;
                saveSettings();
            } else {
                settings = ev.detail.value;
            }
            e.fire('ready');
        }

        function onWrite(ev) {
            if (ev.detail.key !== STORAGE_KEY) {
                return;
            }
            e.fire('save', ev.detail.value);
        }

        /**
         * Make sure that init is run when storage is ready
         */
        function runInit() {
            if (s.isReady()) {
                init();
            } else {
                e.listen('core.storage.idb.open', init);
            }
        }

        e.on({
            'core.storage.idb.write': onWrite,
            'core.storage.idb.read': onRead
        });

        return {
            init: runInit,
            getUnit: getUnit,
            setUnit: setUnit
        };
    }

});
