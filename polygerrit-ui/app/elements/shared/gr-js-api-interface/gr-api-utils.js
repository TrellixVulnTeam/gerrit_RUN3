/**
 * @license
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function(window) {
  'use strict';

  const PRELOADED_PROTOCOL = 'preloaded:';

  let _restAPI;

  const getRestAPI = () => {
    if (!_restAPI) {
      _restAPI = document.createElement('gr-rest-api-interface');
    }
    return _restAPI;
  };

  function getPluginNameFromUrl(url) {
    if (!(url instanceof URL)) {
      try {
        url = new URL(url);
      } catch (e) {
        console.warn(e);
        return null;
      }
    }
    if (url.protocol === PRELOADED_PROTOCOL) {
      return url.pathname;
    }
    const base = Gerrit.BaseUrlBehavior.getBaseUrl();
    const pathname = url.pathname.replace(base, '');
    // Site theme is server from predefined path.
    if (pathname === '/static/gerrit-theme.html') {
      return 'gerrit-theme';
    } else if (!pathname.startsWith('/plugins')) {
      console.warn('Plugin not being loaded from /plugins base path:',
          url.href, '— Unable to determine name.');
      return null;
    }
    // Pathname should normally look like this:
    // /plugins/PLUGINNAME/static/SCRIPTNAME.html
    // Or, for app/samples:
    // /plugins/PLUGINNAME.html
    return pathname.split('/')[2].split('.')[0];
  }

  // TODO (viktard): deprecate in favor of GrPluginRestApi.
  function send(method, url, opt_callback, opt_payload) {
    return getRestAPI().send(method, url, opt_payload).then(response => {
      if (response.status < 200 || response.status >= 300) {
        return response.text().then(text => {
          if (text) {
            return Promise.reject(text);
          } else {
            return Promise.reject(response.status);
          }
        });
      } else {
        return getRestAPI().getResponseObject(response);
      }
    }).then(response => {
      if (opt_callback) {
        opt_callback(response);
      }
      return response;
    });
  }

  function resetInternalState() {
    _restAPI = null;
  }

  window._apiUtils = {
    getPluginNameFromUrl,
    send,
    getRestAPI,

    // TEST only methods
    resetInternalState,
  };
})(window);