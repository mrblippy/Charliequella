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

package com.tle.admin.dynacollection.tool;

import com.tle.admin.baseentity.BaseEntityEditor;
import com.tle.admin.dynacollection.DynaCollectionEditor;
import com.tle.admin.tools.common.BaseEntityTool;
import com.tle.beans.entity.DynaCollection;
import com.tle.common.applet.client.ClientService;
import com.tle.common.dynacollection.RemoteDynaCollectionService;
import com.tle.core.remoting.RemoteAbstractEntityService;

public class DynaCollectionTool extends BaseEntityTool<DynaCollection> {
  public DynaCollectionTool() throws Exception {
    super(DynaCollection.class, RemoteDynaCollectionService.ENTITY_TYPE);
  }

  @Override
  protected RemoteAbstractEntityService<DynaCollection> getService(ClientService client) {
    return client.getService(RemoteDynaCollectionService.class);
  }

  @Override
  protected String getErrorPath() {
    return "dynacollection"; //$NON-NLS-1$
  }

  @Override
  protected BaseEntityEditor<DynaCollection> createEditor(boolean readonly) {
    return new DynaCollectionEditor(this, readonly);
  }

  @Override
  protected String getEntityName() {
    return getString("entityname"); // $NON-NLS-1$
  }
}
