/*global define, tizen*/
/*jslint plusplus: true */

/**
 * Page helper module
 */

define({
    name: 'helpers/page',
    requires: [
    ],
    def: function helpersPage() {
        'use strict';

        var width = 0,
            height = 0;

        /**
         * Checks if the page is active.
         * @param {HTMLElement} page
         * @return {boolean}
         */
        function isPageActive(page) {
            return page.classList.contains('ui-page-active');
        }

        /**
         * Sets page dimension.
         * @param {object} value
         */
        function setPageDimension(value) {
            if (typeof value === 'object' && value.width && value.height) {
                width = value.width;
                height = value.height;
            }
        }

        /**
         * Returns page dimension.
         * @return {object}
         */
        function getPageDimension() {
            return {
                width: width,
                height: height
            };
        }

        return {
            isPageActive: isPageActive,
            setPageDimension: setPageDimension,
            getPageDimension: getPageDimension
        };
    }
});
