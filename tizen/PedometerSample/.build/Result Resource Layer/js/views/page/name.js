/*global define, document, history, tau*/

/**
 * Name page module
 */

define({
    name: 'views/page/name',
    requires: [
        'core/event',
        'models/routes',
        'helpers/page',
        'helpers/route'
    ],
    def: function viewsPageName(req) {
        'use strict';

        var e = req.core.event,
            routes = req.models.routes,
            pageHelper = req.helpers.page,
            routeHelper = req.helpers.route,

            page = null,
            saveBtn = null,
            routeName = '',

            route = null;

        /**
         * Uses received params and shows delete page.
         * @param {object} params
         */
        function show(params) {
            route = params.detail.route;
            routeName.value = routeHelper.getRouteName(route);
            tau.changePage('#name', {fromHashChange: true});
        }

        /**
         * Hides page and save route.
         */
        function hide() {
            if (routeName.value.trim().length > 0) {
                route.name = routeName.value;
            }
            routes.add(route);
            routeName.blur();
        }

        /**
         * Handles pageshow event.
         */
        function onPageShow() {
            routeName.focus();
        }

        /**
         * Handles keyup event on text input element.
         * @param {event} ev
         */
        function onNameKeyUp(ev) {
            if (ev.keyCode === 13) {
                if (routeName.value) {
                    hide();
                }
            }
        }

        /**
         * Binds events.
         */
        function bindEvents() {
            page.addEventListener('pageshow', onPageShow);
            routeName.addEventListener('keyup', onNameKeyUp);
            saveBtn.addEventListener('click', hide);
        }

        /**
         * Initializes module.
         */
        function init() {
            page = document.getElementById('name');
            routeName = document.getElementById('name-text');
            saveBtn = document.getElementById('save-btn');
            bindEvents();
        }

        e.on({
            'views.page.register.show.name': show,
            'views.page.init.name.hide': hide
        });

        return {
            init: init
        };
    }

});
