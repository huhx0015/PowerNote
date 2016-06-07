/*global define, $, console, window, document, history, tau*/
/*jslint plusplus: true*/

/**
 * Details page module
 */

define({
    name: 'views/page/details',
    requires: [
        'core/event',
        'models/routes',
        'models/settings',
        'helpers/timer',
        'helpers/units',
        'helpers/calculations',
        'helpers/route'
    ],
    def: function viewsPageDetails(req) {
        'use strict';

        var e = req.core.event,
            settings = req.models.settings,
            routes = req.models.routes,
            Time = req.helpers.timer.Time,
            units = req.helpers.units,
            r = req.helpers.route,
            calculations = req.helpers.calculations,
            route = {},
            page = null,
            elRouteName = null,
            elDeleteButton = null,
            elSteps = null,
            elHours = null,
            elMinutes = null,
            elSeconds = null,
            elDistance = null,
            elDistanceUnit = null,
            elSpeedUnit = null,
            elSpeed = null,
            routeId = 0,
            digits = [0, 0, 0, 0, 0, 0];

        function refreshSteps(value) {
            elSteps.innerHTML = value;
        }

        function refreshHours() {
            var content = digits[0].toString() + digits[1];
            elHours.innerHTML = content;
        }

        function refreshMinutes() {
            var content = digits[2].toString() + digits[3];
            elMinutes.innerHTML = content;
        }

        function refreshSeconds() {
            var content = digits[4].toString() + digits[5];
            elSeconds.innerHTML = content;
        }

        function refreshTimer(timeMilliseconds) {
            var time = [], i = 0;
            time = new Time(timeMilliseconds);

            i = 6;
            while (i--) {
                digits[i] = time[i];
            }

            refreshHours();
            refreshMinutes();
            refreshSeconds();
        }

        function refreshSpeedAndDistance(route) {
            var distance = route.distance,
                time = route.time,
                unit = settings.getUnit(),
                avaregeSpeed = calculations.getSpeed(distance, time);

            if (unit === units.UNIT_FEET) {
                avaregeSpeed = units.getMiles(avaregeSpeed);
                distance = units.getFeet(distance);
            }

            elDistance.innerHTML = distance.toFixed(1);
            elSpeed.innerHTML = avaregeSpeed.toFixed(2);
        }

        function show(params) {
            var data = params.detail,
                steps = 0,
                time = 0;

            // routeID must be a number, not a string
            routeId = parseInt(data.id, 10);
            route = routes.get(routeId);

            if (!route) {
                history.back();
            } else {
                steps = route.steps;
                time = route.time;

                if (r.isEmptyRouteName(route)) {
                    elRouteName.innerHTML = r.getRouteName(route);
                } else {
                    elRouteName.innerHTML = route.name;
                }

                refreshSteps(steps);
                refreshTimer(time);
                refreshSpeedAndDistance(route);
                setSpeedAndDinstanceUnit();

                tau.changePage('#details', {
                    fromHashChange: data.fromHashChange
                });
            }
        }

        function onDelete(ev) {
            ev.preventDefault();
            ev.stopPropagation();
            e.fire('delete.route', {
                route: route
            });
        }

        function setSpeedAndDinstanceUnit() {
            var distanceUnit = units.UNIT_METER,
                speedUnit = units.UNIT_METER_SPEED;

            if (settings.getUnit() === units.UNIT_FEET) {
                distanceUnit = units.UNIT_FEET;
                speedUnit = units.UNIT_FEET_SPEED;
            }

            elDistanceUnit.innerHTML = distanceUnit;
            elSpeedUnit.innerHTML = speedUnit;
        }

        function bindEvents() {
            elDeleteButton.addEventListener('click', onDelete);
        }

        /**
         * Initializes module.
         */
        function init() {
            page = document.getElementById('details');
            elRouteName = page.getElementsByClassName('ui-title')[0];
            elDeleteButton = page.getElementsByClassName('delete')[0];
            elSteps = page.querySelector('.steps .value');
            elHours = page.querySelector('.time .hours');
            elMinutes = page.querySelector('.time .minutes');
            elSeconds = page.querySelector('.time .seconds');
            elDistance = page.querySelector('.distance .value');
            elDistanceUnit = page.querySelector('.distance-unit');
            elSpeedUnit = page.querySelector('.speed-unit');
            elSpeed = page.querySelector('.speed .value');
            bindEvents();
        }

        e.on({
            'views.page.history.details.show': show,
            'views.page.delete.details.show': show,
            'views.page.register.details.show': show
        });

        return {
            init: init
        };
    }

});
