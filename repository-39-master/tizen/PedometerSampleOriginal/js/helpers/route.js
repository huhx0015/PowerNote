/*global define, tizen*/
/*jslint plusplus: true */

/**
 * Route helper module
 */

define({
    name: 'helpers/route',
    requires: [
        'helpers/date'
    ],
    def: function helpersRoute(date) {
        'use strict';

        function isEmptyRouteName(route) {
            if (route) {
                return route.name === undefined || route.name.trim() === '';
            }
        }

        /**
         * Returns route name.
         * @param {object} route
         * @return {string}
         */
        function getRouteName(route) {
            var routeName = null,
                routeDate = null;

            if (route.date) {
                routeDate = new Date(route.date);
                routeName = date.format(routeDate, 'h:MMTT ddd, mmm d');
            } else {
                routeName = route.id;
            }

            return routeName;
        }

        /**
         * Sorts descending routes data (by route id).
         * @param {array} routes
         * @return {array}
         */
        function sortDescending(routes) {
            routes.sort(function (a, b) {
                if (a.id < b.id) {
                    return 1;
                } else if (a.id > b.id) {
                    return -1;
                } else {
                    return 0;
                }
            });

            return routes;
        }

        return {
            isEmptyRouteName: isEmptyRouteName,
            getRouteName: getRouteName,
            sortDescending: sortDescending
        };
    }
});
