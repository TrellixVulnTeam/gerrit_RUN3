// Copyright (C) 2016 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
(function() {
  'use strict';

  Polymer({
    is: 'gr-app',

    properties: {
      params: Object,
      keyEventTarget: {
        type: Object,
        value: function() { return document.body; },
      },

      _account: {
        type: Object,
        observer: '_accountChanged',
      },
      _serverConfig: Object,
      _version: String,
      _preferences: Object,
      _showChangeListView: Boolean,
      _showDashboardView: Boolean,
      _showChangeView: Boolean,
      _showDiffView: Boolean,
      _viewState: Object,
    },

    listeners: {
      'title-change': '_handleTitleChange',
    },

    observers: [
      '_viewChanged(params.view)',
    ],

    behaviors: [
      Gerrit.KeyboardShortcutBehavior,
    ],

    get loggedIn() {
      return !!(this._account && Object.keys(this._account).length > 0);
    },

    attached: function() {
      this.$.restAPI.getAccount().then(function(account) {
        this._account = account;
      }.bind(this));
      this.$.restAPI.getConfig().then(function(config) {
        this._serverConfig = config;
      }.bind(this));
      this.$.restAPI.getVersion().then(function(version) {
        this._version = version;
      }.bind(this));
    },

    ready: function() {
      this._viewState = {
        changeView: {
          changeNum: null,
          patchNum: null,
          selectedFileIndex: 0,
          showReplyDialog: false,
        },
        changeListView: {
          query: null,
          offset: 0,
          selectedChangeIndex: 0,
        },
        dashboardView: {
          selectedChangeIndex: 0,
        },
      };
    },

    _accountChanged: function(account) {
      if (this.loggedIn) {
        this.$.restAPI.getPreferences().then(function(preferences) {
          this._preferences = preferences;
        }.bind(this));
        // Diff preferences are cached; warm it before a diff is rendered.
        this.$.restAPI.getDiffPreferences();
      } else {
        this._preferences = {
          changes_per_page: 25,
        };
      }
    },

    _viewChanged: function(view) {
      this.set('_showChangeListView', view === 'gr-change-list-view');
      this.set('_showDashboardView', view === 'gr-dashboard-view');
      this.set('_showChangeView', view === 'gr-change-view');
      this.set('_showDiffView', view === 'gr-diff-view');
    },

    _loginTapHandler: function(e) {
      e.preventDefault();
      page.show('/login/' + encodeURIComponent(
          window.location.pathname + window.location.hash));
    },

    _computeLoggedIn: function(account) { // argument used for binding update only
      return this.loggedIn;
    },

    _handleTitleChange: function(e) {
      if (e.detail.title) {
        document.title = e.detail.title + ' · Gerrit Code Review';
      } else {
        document.title = '';
      }
    },

    _handleKey: function(e) {
      if (this.shouldSupressKeyboardShortcut(e)) { return; }

      if (e.keyCode === 191 && e.shiftKey) {  // '/' or '?' with shift key.
        this.$.keyboardShortcuts.open();
      }
    },

    _handleKeyboardShortcutDialogClose: function() {
      this.$.keyboardShortcuts.close();
    },
  });
})();
