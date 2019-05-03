/*
 * Copyright 2017 Apereo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tle.admin.controls;

import com.dytech.edge.admin.wizard.editor.AbstractControlEditor;
import com.dytech.edge.admin.wizard.model.Control;
import com.dytech.edge.wizard.beans.control.WizardControl;
import com.tle.admin.i18n.Lookup;
import com.tle.admin.schema.SchemaModel;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UnavailableControlEditor extends AbstractControlEditor<WizardControl> {

  public UnavailableControlEditor(Control control, int wizardType, SchemaModel schema) {
    super(control, wizardType, schema);
    // if this UnavailableControl was originally a CloudControl then display an error message
    if (control.getDefinition().getId().startsWith("cp.")) {
      setupGUI();
    }
  }

  @Override
  protected void saveControl() {}

  @Override
  protected void loadControl() {}

  private void setupGUI() {
    JPanel body = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel text = new JLabel(Lookup.lookup.text("unavailablecontrol.message"));
    body.add(text);
    addSection(body);
  }
}