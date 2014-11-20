/*global define, $, console, window, history, isNaN*/

/**
 * Meter helper module
 */

define({
    name: 'helpers/calculations',
    def: function helpersCalculations() {
        'use strict';

        var MS_TO_S = 1000,
            KM_PER_HOUR = 3.6,
            MILES_PER_HOUR = 0.6818184;

        function getDistance(value, count) {
            return value * count;
        }

        function getSpeed(distance, time) {
            var speed = 0;

            time = time / MS_TO_S;
            speed = distance / time * KM_PER_HOUR;
            speed = isNaN(speed) ? 0 : speed;
            return speed;
        }

        return {
            getDistance: getDistance,
            getSpeed: getSpeed
        };
    }

});
