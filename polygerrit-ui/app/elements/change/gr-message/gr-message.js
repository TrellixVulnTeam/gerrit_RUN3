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
    is: 'gr-message',

    /**
     * Fired when this message's permalink is tapped.
     *
     * @event scroll-to
     */

    /**
     * Fired when this message's reply link is tapped.
     *
     * @event reply
     */

    listeners: {
      'tap': '_handleTap',
    },

    properties: {
      changeNum: Number,
      message: Object,
      author: {
        type: Object,
        computed: '_computeAuthor(message)',
      },
      comments: {
        type: Object,
        observer: '_commentsChanged',
      },
      config: Object,
      hideAutomated: {
        type: Boolean,
        value: false,
      },
      hidden: {
        type: Boolean,
        computed: '_computeIsHidden(hideAutomated, isAutomated)',
        reflectToAttribute: true,
      },
      isAutomated: {
        type: Boolean,
        computed: '_computeIsAutomated(message)',
      },
      showAvatar: {
        type: Boolean,
        computed: '_computeShowAvatar(author, config)',
      },
      showReplyButton: {
        type: Boolean,
        computed: '_computeShowReplyButton(message, _loggedIn)',
      },
      projectConfig: Object,
      // Computed property needed to trigger Polymer value observing.
      _expanded: {
        type: Object,
        computed: '_computeExpanded(message.expanded)',
      },
      _loggedIn: {
        type: Boolean,
        value: false,
      },
    },

    ready: function() {
      this.$.restAPI.getConfig().then(function(config) {
        this.config = config;
      }.bind(this));
      this.$.restAPI.getLoggedIn().then(function(loggedIn) {
        this._loggedIn = loggedIn;
      }.bind(this));
    },

    _computeAuthor: function(message) {
      return message.author || message.updated_by;
    },

    _computeShowAvatar: function(author, config) {
      return !!(author && config && config.plugin && config.plugin.has_avatars);
    },

    _computeShowReplyButton: function(message, loggedIn) {
      return !!message.message && loggedIn;
    },

    _computeExpanded: function(expanded) {
      return expanded;
    },

    /**
     * If there is no value set on the message object as to whether _expanded
     * should be true or not, then _expanded is set to true if there are
     * inline comments (otherwise false).
     */
    _commentsChanged: function(value) {
      if (this.message && this.message.expanded === undefined) {
        this.set('message.expanded', Object.keys(value || {}).length > 0);
      }
    },

    _handleTap: function(e) {
      if (this.message.expanded) { return; }
      e.stopPropagation();
      this.set('message.expanded', true);
    },

    _handleNameTap: function(e) {
      if (!this.message.expanded) { return; }
      e.stopPropagation();
      this.set('message.expanded', false);
    },

    _computeIsAutomated: function(message) {
      return !!(message.reviewer ||
          (message.tag && message.tag.indexOf('autogenerated') === 0));
    },

    _computeIsHidden: function(hideAutomated, isAutomated) {
      return hideAutomated && isAutomated;
    },

    _computeIsReviewerUpdate: function(event) {
      return event.type === 'REVIEWER_UPDATE';
    },

    _computeClass: function(expanded, showAvatar) {
      var classes = [];
      classes.push(expanded ? 'expanded' : 'collapsed');
      classes.push(showAvatar ? 'showAvatar' : 'hideAvatar');
      return classes.join(' ');
    },

    _computeMessageHash: function(message) {
      return '#message-' + message.id;
    },

    _handleLinkTap: function(e) {
      e.preventDefault();

      this.fire('scroll-to', {message: this.message}, {bubbles: false});

      var hash = this._computeMessageHash(this.message);
      // Don't add the hash to the window history if it's already there.
      // Otherwise you mess up expected back button behavior.
      if (window.location.hash == hash) { return; }
      // Change the URL but don’t trigger a nav event. Otherwise it will
      // reload the page.
      page.show(window.location.pathname + hash, null, false);
    },

    _handleReplyTap: function(e) {
      e.preventDefault();
      this.fire('reply', {message: this.message});
    },
  });
})();
