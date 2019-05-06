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

package com.tle.core.portal.dao;

import com.tle.common.portal.entity.Portlet;
import com.tle.common.portal.entity.PortletPreference;
import com.tle.core.hibernate.dao.GenericInstitutionalDao;
import java.util.Collection;
import java.util.List;

/** @author aholland */
public interface PortletPreferenceDao extends GenericInstitutionalDao<PortletPreference, Long> {
  PortletPreference getForPortlet(String userId, Portlet portlet);

  /** Generally only useful in an export preferences context */
  List<PortletPreference> getAllForPortlet(final Portlet portlet);

  List<PortletPreference> getForPortlets(String userId, Collection<Portlet> portlets);

  int deleteAllForPortlet(Portlet portlet);

  void deleteAllForUser(String userId);

  void changeUserId(String fromUserId, String toUserId);
}
