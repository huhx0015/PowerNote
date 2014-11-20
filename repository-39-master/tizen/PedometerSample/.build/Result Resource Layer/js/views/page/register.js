/*global define, $, console, window, document, history, tau*/
/*jslint plusplus: true*/

/**
 * Register page module
 */

define({
    name: 'views/page/register',
    requires: [
        'core/event',
        'models/routes',
        'models/timer',
        'models/settings',
        'models/pedometer',
        'helpers/timer',
        'helpers/units',
        'helpers/page',
        'helpers/route'
    ],
    def: function viewsPageRegister(req) {
        'use strict';

        var e = req.core.event,
            pedometer = req.models.pedometer,
            settings = req.models.settings,
            routes = req.models.routes,
            Timer = req.models.timer.Timer,
            Time = req.helpers.timer.Time,
            units = req.helpers.units,
            pageHelper = req.helpers.page,

            BASE_WIDTH = 320,
            BASE_HEIGHT = 320,

            page = null,
            stopButton = null,
            elSteps = null,
            elHours = null,
            elMinutes = null,
            elSeconds = null,
            elDistance = null,
            elDistanceUnit = null,
            elSpeed = null,
            elSpeedUnit = null,

            timer = null,

            digits = [0, 0, 0, 0, 0, 0],
            changePageAfterRouteSaved = true;

        /**
         * Handles views.page.main.register.show event.
         */
        function show() {
            tau.changePage('#register');
        }

        /**
         * Updates UI steps.
         * @param {numeric} value
         */
        function updateSteps(value) {
            elSteps.innerHTML = value;
        }

        /**
         * Updates UI speed.
         * @param {numeric} speed
         */

        function updateSpeed(speed) {
            elSpeed.innerHTML = speed;
        }

        /**
         * Updates UI distance.
         * @param {numeric} distance
         */

        function updateDistance(distance) {
            elDistance.innerHTML = distance;
        }

        /**
         * Updates UI speed unit.
         * @param {string} unit
         */
        function updateSpeedUnit(unit) {
            elSpeedUnit.innerHTML = unit;
        }

        /**
         * Updates UI distance unit.
         * @param {string} { unit }
         */
        function updateDistanceUnit(unit) {
            elDistanceUnit.innerHTML = unit;
        }

        /**
         * Updates tmp. distance.
         */
        function updateTemporaryDistance(distance) {
            var unit = settings.getUnit();

            if (typeof distance !== 'number') {
                distance = parseFloat(distance);
            }

            if (unit === units.UNIT_FEET) {
                distance = units.getFeet(distance);
            }

            updateDistance(distance.toFixed(1));
        }

        /**
         * Updates tmp. speed.
         */
        function updateTemporarySpeed(speed) {
            var unit = settings.getUnit();

            if (typeof speed !== 'number') {
                speed = parseFloat(speed);
            }

            if (unit === units.UNIT_FEET) {
                speed = units.getMiles(speed);
            }

            updateSpeed(speed.toFixed(2));
        }

        /**
         * Adds data from pedometer.
         * @param {boolean} mode (false for show keyboard)
         */
        function stop(mode) {
            var currentDate = new Date(),
                lastData = pedometer.getData(),
                pageDimension = pageHelper.getPageDimension(),
                route = {
                    steps: lastData.totalStep,
                    distance: lastData.distance,
                    time: timer.getTimeElapsed(),
                    date: currentDate.toJSON()
                };
            if (mode || pageDimension.width === BASE_WIDTH &&
                    pageDimension.height === BASE_HEIGHT) {
                routes.add(route);
            } else {
                e.fire('show.name', { route: route });
            }
        }

        /**
         * Handles tap on stop button.
         * @param {Event} ev
         */
        function onStop(ev) {
            ev.preventDefault();
            ev.stopPropagation();

            changePageAfterRouteSaved = true;
            stop(false);
        }

        /**
         * Handles back from pedometer.
         */
        function onMenuBack() {
            changePageAfterRouteSaved = false;
            stop(true);
        }

        /**
         * Refreshes UI hours.
         */
        function refreshHours() {
            var content = digits[0].toString() + digits[1];
            elHours.innerHTML = content;
        }
        /**
         * Refreshes UI minutes.
         */
        function refreshMinutes() {
            var content = digits[2].toString() + digits[3];
            elMinutes.innerHTML = content;
        }

        /**
         * Refreshes UI seconds.
         */
        function refreshSeconds() {
            var content = digits[4].toString() + digits[5];
            elSeconds.innerHTML = content;
        }

        /**
         * Refreshes Timer.
         * @param {number} timeMilliseconds
         */
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

        /**
         * Sets distance unit.
         */
        function setDistanceUnit() {
            var unit = units.UNIT_METER;

            if (settings.getUnit() === units.UNIT_FEET) {
                unit = units.UNIT_FEET;
            }

            updateDistanceUnit(unit);
        }

        /**
         * Sets speed unit.
         */
        function setSpeedUnit() {
            var unit = units.UNIT_METER_SPEED;

            if (settings.getUnit() === units.UNIT_FEET) {
                unit = units.UNIT_FEET_SPEED;
            }

            updateSpeedUnit(unit);
        }

        /**
         * Handles pagebeforeshow event.
         */
        function onPageBeforeShow() {
            changePageAfterRouteSaved = true;
            updateSteps(0);
            updateSpeed('0.00');
            updateDistance('0.0');
            setDistanceUnit();
            setSpeedUnit();
            refreshTimer(0);
        }

        /**
         * Handles pageshow event.
         */
        function onPageShow() {
            pageHelper.setPageDimension({
                width: window.innerWidth,
                height: window.innerHeight
            });

            timer = new Timer(1000, 'views.register.tick');
            timer.run();
            pedometer.start();
        }

        /**
         * Handles pagehide event.
         */
        function onPageHide() {
            timer.reset();
            pedometer.stop();
        }

        /**
         * Registers event listeners.
         */
        function bindEvents() {
            page.addEventListener('pagebeforeshow', onPageBeforeShow);
            page.addEventListener('pageshow', onPageShow);
            page.addEventListener('pagehide', onPageHide);
            stopButton.addEventListener('click', onStop);
        }

        /**
         * Handles models.timer.views.register.tick event.
         */
        function tick() {
            refreshTimer(timer.getTimeElapsed());
        }

        /**
         * Handles pedometer info change.
         * @param {object} param
         */
        function onPedometerInfoChange(param) {
            var pedometerInfo = param.detail;

            updateSteps(pedometerInfo.totalStep);
            updateTemporarySpeed(pedometerInfo.speed);
            updateTemporaryDistance(pedometerInfo.distance);
        }

        /**
         * Handles routes.add.success event.
         */
        function onRouteAddSuccess() {
            var route = null;
            if ((pageHelper.isPageActive(document.getElementById('name')) ||
                pageHelper.isPageActive(document.getElementById('register'))) &&
                    changePageAfterRouteSaved) {
                route = routes.getLastInserted();
                e.fire('details.show', {
                    id: route.id,
                    fromHashChange: true
                });
            }
        }

        /**
         * Handles application.exit event.
         */
        function onApplicationExit() {
            if (pageHelper.isPageActive(page)) {
                changePageAfterRouteSaved = false;
                stop();
            }
        }

        /**
         * Initializes module.
         */
        function init() {
            page = document.getElementById('register');
            stopButton = page.querySelector('.stop');
            elSteps = page.querySelector('.steps .value');
            elHours = page.querySelector('.time .hours');
            elMinutes = page.querySelector('.time .minutes');
            elSeconds = page.querySelector('.time .seconds');
            elDistance = page.querySelector('.distance .value');
            elDistanceUnit = page.querySelector('.distance-unit');
            elSpeed = page.querySelector('.speed .value');
            elSpeedUnit = page.querySelector('.speed-unit');
            bindEvents();
        }

        e.on({
            'views.page.init.register.menuBack': onMenuBack,
            'models.pedometer.change': onPedometerInfoChange,
            'models.timer.views.register.tick': tick,
            'views.page.main.register.show': show,
            'views.page.init.application.exit': onApplicationExit,
            'models.routes.add.success': onRouteAddSuccess
        });

        return {
            init: init
        };
    }

});
