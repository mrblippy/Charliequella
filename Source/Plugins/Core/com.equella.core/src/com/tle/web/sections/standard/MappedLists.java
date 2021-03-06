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

package com.tle.web.sections.standard;

import com.tle.web.sections.SectionInfo;
import com.tle.web.sections.SectionUtils;
import com.tle.web.sections.standard.model.HtmlListState;
import com.tle.web.sections.standard.model.Option;
import java.util.Collections;
import java.util.List;

public class MappedLists extends AbstractMappedValues<String> {
  public HtmlListState getListState(
      SectionInfo info, String key, List<Option<?>> options, String defaultSelectedValue) {
    final HtmlListState state = new HtmlListState();
    state.setId(getParameterId() + '_' + SectionUtils.getPageUniqueId(info));
    state.setName(getParameterId() + '(' + keyEscape(key) + ')');
    state.setMultiple(false);
    state.setOptions(options);

    final MappedValuesModel<String> model = getModel(info);

    String selectedValue = model.getMap().get(key);
    if (selectedValue == null) {
      selectedValue = defaultSelectedValue;
    }

    if (selectedValue != null) {
      state.setSelectedValues(Collections.singleton(selectedValue));
    }

    model.getRendered().add(key);
    return state;
  }

  public String getSelectedValue(SectionInfo info, String key) {
    final MappedValuesModel<String> model = getModel(info);
    return model.getMap().get(key);
  }

  @Override
  protected String convert(String val) {
    return val;
  }

  @Override
  protected String convertBack(String val) {
    return val;
  }
}
