/*
 * Licensed to The Apereo Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * The Apereo Foundation licenses this file to you under the Apache License,
 * Version 2.0, (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tle.web.portal.standard.editor.tabs;

import com.tle.annotation.NonNullByDefault;
import com.tle.core.portal.service.PortletEditingBean;
import com.tle.web.sections.SectionInfo;
import com.tle.web.sections.js.JSStatements;
import com.tle.web.sections.standard.model.TabSection;
import java.util.Map;

@NonNullByDefault
public interface ScriptingTabInterface extends TabSection {
  void customLoad(SectionInfo info, PortletEditingBean portlet);

  void customSave(SectionInfo info, PortletEditingBean portlet);

  void customClear(SectionInfo info);

  void customValidate(SectionInfo info, Map<String, Object> errors);

  JSStatements getTabShowStatements();
}
