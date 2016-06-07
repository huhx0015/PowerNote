/*global define, $, console, window, history, document, tau*/

/**
 * Init page module
 */

define({
    name: 'views/page/init',
    requires: [
        'core/event',
        'core/template',
        'core/systeminfo',
        'core/application',
        'core/storage/idb',
        'views/page/main',
        'views/page/details',
        'views/page/units',
        'views/page/delete',
        'views/page/name'
    ],
    def: function viewsPageInit(req) {
        'use strict';

        var e = req.core.event,
            idb = req.core.storage.idb,
            app = req.core.application,
            sysInfo = req.core.systeminfo;

        /**
         * Exits the application, waiting first for pending storage requests
         * to complete.
         */
        function exit() {
            e.fire('application.exit');
            if (!idb.hasPendingRequests()) {
                app.exit();
            } else {
                e.listen('core.storage.idb.completed', app.exit);
            }
        }

        /**
         * Handles tizenhwkey event.
         * @param {event} ev
         */
        function onHardwareKeysTap(ev) {
            var keyName = ev.keyName,
                page = document.getElementsByClassName('ui-page-active')[0],
                pageid = page ? page.id : '';

            if (keyName === 'back') {
                if (pageid === 'main' || pageid === 'ajax-loader') {
                    exit();
                } else if (pageid === 'delete') {
                    tau.changePage('#details', {fromHashChange: true});
                } else if (pageid === 'settings') {
                    tau.changePage('#main');
                } else if (pageid === 'name') {
                    e.fire('name.hide');
                } else if (pageid === 'register') {
                    e.fire('register.menuBack');
                    history.back();
                } else {
                    history.back();
                }
            }
        }

        /**
         * Handles resize event.
         */
        function onWindowResize() {
            e.fire('window.resize', { height: window.innerHeight });
        }

        /**
         * Handler onLowBattery state
         */
        function onLowBattery() {
            if (document.getElementsByClassName('ui-page-active')[0].id ===
                'register') {
                e.fire('register.menuBack');
            }
            exit();
        }

        /**
         * Registers event listeners.
         */
        function bindEvents() {
            window.addEventListener('tizenhwkey', onHardwareKeysTap);
            window.addEventListener('resize', onWindowResize);
            sysInfo.listenBatteryLowState();
        }

        /**
         * Initializes module.
         */
        function init() {
            // bind events to page elements
            bindEvents();
            sysInfo.checkBatteryLowState();
        }

        e.on({
            'core.systeminfo.battery.low': onLowBattery
        });

        return {
            init: init
        };
    }

});
