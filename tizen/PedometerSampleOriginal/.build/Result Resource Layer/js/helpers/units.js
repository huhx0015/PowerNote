/*global define, $, console, window, history*/

/**
 * Meter helper module
 */

define({
    name: 'helpers/units',
    def: function helpersUnits() {
        'use strict';

        var METER_TO_FEET = 3.2808399,
            KM_TO_MILES = 0.621371192,

            UNIT_METER = 'm',
            UNIT_FEET = 'ft',
            UNIT_METER_SPEED = 'km/h',
            UNIT_FEET_SPEED = 'mph',
            UNITS = {};

        UNITS[UNIT_METER] = {label: 'm'};
        UNITS[UNIT_FEET] = {label: 'ft'};

        function getFeet(value) {
            return value * METER_TO_FEET;
        }

        function getMiles(value) {
            return value * KM_TO_MILES;
        }

        function getUnit(unit) {
            return UNITS[unit] || null;
        }

        return {
            UNIT_METER: UNIT_METER,
            UNIT_FEET: UNIT_FEET,
            UNIT_METER_SPEED: UNIT_METER_SPEED,
            UNIT_FEET_SPEED: UNIT_FEET_SPEED,

            getFeet: getFeet,
            getMiles: getMiles,
            getUnit: getUnit
        };
    }

});
