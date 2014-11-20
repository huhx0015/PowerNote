/*global define, $, console, window, document, history, tau*/

/**
 * Delete page module
 */

define({
    name: 'views/page/delete',
    requires: [
        'core/event',
        'models/routes',
        'helpers/route',
        'helpers/page'
    ],
    def: function viewsPageDelete(req) {
        'use strict';

        var e = req.core.event,
            routes = req.models.routes,
            r = req.helpers.route,
            pageHelper = req.helpers.page,

            page = null,
            yesButton,
            noButton,
            route,
            removingInProgress = false;

        /**
         * Uses received params and shows delete page.
         * @param {object} params
         */
        function show(params) {
            route = params.detail.route;

            tau.changePage('#delete', {fromHashChange: true});
        }

        /**
         * Handles tap event on Yes button.
         */
        function onDelete(ev) {
            ev.preventDefault();
            ev.stopPropagation();
            if (!removingInProgress) {
                removingInProgress = true;
                routes.remove(route.id);
            }

        }

        /**
         * Handles tap event on No button.
         */
        function onBack(ev) {
            ev.preventDefault();
            ev.stopPropagation();
            tau.changePage('#details', {fromHashChange: true});
        }

        /**
         * Handles pageshow event.
         */
        function onPageShow() {
            removingInProgress = false;
        }

        /**
         * Handles routes.remove.success event.
         */
        function onRouteRemoveSuccess() {
            if (pageHelper.isPageActive(page)) {
                removingInProgress = false;
                e.fire('details.show', {id: route.id});
            }
        }

        /**
         * Binds events.
         */
        function bindEvents() {
            page.addEventListener('pageshow', onPageShow);
            yesButton.addEventListener('click', onDelete);
            noButton.addEventListener('click', onBack);
        }

        /**
         * Initializes module.
         */
        function init() {
            page = document.getElementById('delete');
            yesButton = page.getElementsByClassName('yes')[0];
            noButton = page.getElementsByClassName('no')[0];
            bindEvents();
        }

        e.on({
            'views.page.details.delete.route': show,
            'models.routes.remove.success': onRouteRemoveSuccess
        });

        return {
            init: init
        };
    }

});
