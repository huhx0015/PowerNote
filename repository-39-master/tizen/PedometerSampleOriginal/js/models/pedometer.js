/*global define, $, console, window, tizen, webapis*/
/*jslint regexp: true, plusplus: true*/

/**
 * Pedometer module
 */

define({
    name: 'models/pedometer',
    requires: [
        'core/event'
    ],
    def: function modelsPedometer(req) {
        'use strict';

        var e = req,
            pedometer = null,
            pedometerData = {},

            CONTEXT_TYPE = 'PEDOMETER';

        /**
         * @param {PedometerInfo} pedometerInfo
         * @return {object} pedometerData
         */
        function getPedometerData(pedometerInfo) {
            var pData = {
                calorie: pedometerInfo.cumulativeCalorie,
                distance: pedometerInfo.cumulativeDistance,
                runDownStep: pedometerInfo.cumulativeRunDownStepCount,
                runStep: pedometerInfo.cumulativeRunStepCount,
                runUpStep: pedometerInfo.cumulativeRunUpStepCount,
                speed: pedometerInfo.speed,
                stepStatus: pedometerInfo.stepStatus,
                totalStep: pedometerInfo.cumulativeTotalStepCount,
                walkDownStep: pedometerInfo.cumulativeWalkDownStepCount,
                walkStep: pedometerInfo.cumulativeWalkStepCount,
                walkUpStep: pedometerInfo.cumulativeWalkUpStepCount,
                walkingFrequency: pedometerInfo.walkingFrequency
            };

            pedometerData = pData;
            return pData;
        }

        /**
         * Return last received motion data
         * @return {object}
         */
        function getData() {
            return pedometerData;
        }

        /**
         * Reset pedometer data
         */
        function resetData() {
            pedometerData = {
                calorie: 0,
                distance: 0,
                runDownStep: 0,
                runStep: 0,
                runUpStep: 0,
                speed: 0,
                stepStatus: '',
                totalStep: 0,
                walkDownStep: 0,
                walkStep: 0,
                walkUpStep: 0,
                walkingFrequency: 0
            };
        }

        /**
         * @param {PedometerInfo} pedometerInfo
         * @param {string} eventName
         */
        function handlePedometerInfo(pedometerInfo, eventName) {
            e.fire(eventName, getPedometerData(pedometerInfo));
        }

        /**
         * Registers a change listener
         * @public
         */
        function start() {
            resetData();
            pedometer.start(
                CONTEXT_TYPE,
                function onSuccess(pedometerInfo) {
                    handlePedometerInfo(pedometerInfo, 'change');
                }
            );
        }

        /**
         * Unregisters a change listener
         * @public
         */
        function stop() {
            pedometer.stop(CONTEXT_TYPE);
        }

        /**
         * Initializes the module
         */
        function init() {
            resetData();

            pedometer = (tizen && tizen.humanactivitymonitor) ||
                (window.webapis && window.webapis.motion) || null;
        }

        return {
            init: init,
            start: start,
            stop: stop,
            getData: getData
        };
    }

});
