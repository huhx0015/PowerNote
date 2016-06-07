/**
 * Routes model.
 */

define({
    name: 'models/routes',
    requires: [
        'core/storage/idb',
        'core/event',
        'helpers/route'
    ],
    def: function modelsRoutes(req) {
        'use strict';

        var s = req.core.storage.idb,
            e = req.core.event,
            r = req.helpers.route,

            counterLoaded = false,
            routesLoaded = false,

            STORAGE_KEY = 'routes',
            STORAGE_COUNTER_KEY = 'routesCounter',
            currId = 0,
            nextId = 0,
            routesData = [],
            addRequestId = null,
            removeRequestId = null;

        /**
         * Saves user routes to local storage.
         * @return {number} Id of save request
         */
        function save() {
            return s.add(STORAGE_KEY, routesData);
        }

        /**
         * Gets next id, which is used to create a new route.
         */
        function getNextId() {
            currId += 1;
            nextId = currId;

            // save the incremented value
            s.add(STORAGE_COUNTER_KEY, nextId); // no need to handle

            // return it
            return nextId;
        }

        /**
         * Returns index for given route
         * @param {number} routeId Route id.
         * @return {number} index Index.
         */
        function getIndex(routeId) {
            var dataLen = routesData.length,
                index = -1,
                i = 0;

            for (i; i < dataLen; i += 1) {
                if (routesData[i].id === routeId) {
                    index = i;
                }
            }

            return index;
        }

        /**
         * Gets route from routes
         * @param {number} routeId Route id.
         * @return {object}
         */
        function get(routeId) {
            var index = getIndex(routeId);
            return routesData[index] || null;
        }

        /**
         * Adds route to routes.
         * @param {object} route.
         */
        function add(route) {
            var dataLen = routesData.length,
                i = 0;

            for (i; i < dataLen; i += 1) {
                if (routesData[i].id === route.id) {
                    return false;
                }
            }

            route.id = getNextId();
            routesData.push(route);

            addRequestId = save();
        }

        /**
         * Removes route from routes
         * @param {number} routeId Route id.
         */
        function remove(routeId) {
            var index = getIndex(routeId);

            if (index !== -1) {
                routesData.splice(index, 1);
                removeRequestId = save();
            }
        }

        /**
         * Returns list of registered routes.
         * @param {bool} [descending=false] Set sort order.
         */
        function getAll(descending) {
            if (descending) {
                return r.sortDescending(routesData);
            }
            return routesData;
        }

        /**
         * Gets last inserted route
         * @return {object}
         */
        function getLastInserted() {
            return routesData[routesData.length - 1] || {};
        }

        /**
         * Handles core.storage.read event.
         */
        function onStorageRead(ev) {
            var key = ev.detail.key,
                val = ev.detail.value;

            if (key === STORAGE_COUNTER_KEY) {
                // stores the loaded currentId in a variable
                currId = val || (routesData ? routesData.length : 0) || 0;
                counterLoaded = true;
            } else if (key === STORAGE_KEY) {
                // store the loaded route data in a variable.
                routesData = val || [];
                routesLoaded = true;
            }
        }

        /**
         * Handles core.storage.write event.
         * @param {event} ev
         */
        function onStorageWriteSuccess(ev) {
            var saveRequestId = ev.detail.id;

            if (saveRequestId === addRequestId) {
                e.fire('add.success');
            } else if (saveRequestId === removeRequestId) {
                e.fire('remove.success');
            }
        }

        function isReady() {
            return counterLoaded && routesLoaded;
        }

        /**
         * Loads all data from the storage.
         */
        function init() {
            s.get(STORAGE_KEY);
            s.get(STORAGE_COUNTER_KEY);
        }

        /**
         * Make sure that init is run when storage is ready
         */
        function runInit() {
            if (s.isReady()) {
                init();
            } else {
                e.listen('core.storage.idb.open', init);
            }
        }

        e.on({
            'core.storage.idb.read': onStorageRead,
            'core.storage.idb.write': onStorageWriteSuccess
        });

        return {
            init: runInit,
            isReady: isReady,
            add: add,
            remove: remove,
            get: get,
            getAll: getAll,
            getLastInserted: getLastInserted
        };
    }

});
