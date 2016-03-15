// Copyright (C) 2016 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the 'License');
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an 'AS IS' BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
(function(window, GrDiffBuilder) {
  'use strict';

  function GrDiffBuilderUnified(diff, outputEl) {
    GrDiffBuilder.call(this, diff, outputEl);
  }
  GrDiffBuilderUnified.prototype = Object.create(GrDiffBuilder.prototype);
  GrDiffBuilderUnified.prototype.constructor = GrDiffBuilderUnified;

  GrDiffBuilderUnified.prototype._emitGroup = function(group,
      opt_beforeSection) {
    var sectionEl = this._createElement('tbody', 'section');

    for (var i = 0; i < group.lines.length; ++i) {
      sectionEl.appendChild(this._createRow(group.lines[i]));
    }
    this._outputEl.insertBefore(sectionEl, opt_beforeSection);
  };

  GrDiffBuilderUnified.prototype._createRow = function(line) {
    var row = this._createElement('tr', line.type);
    row.appendChild(this._createLineEl(line, line.beforeNumber,
        GrDiffLine.Type.REMOVE));
    row.appendChild(this._createLineEl(line, line.afterNumber,
        GrDiffLine.Type.ADD));
    row.appendChild(this._createTextEl(line));
    return row;
  };

  window.GrDiffBuilderUnified = GrDiffBuilderUnified;
})(window, GrDiffBuilder);
