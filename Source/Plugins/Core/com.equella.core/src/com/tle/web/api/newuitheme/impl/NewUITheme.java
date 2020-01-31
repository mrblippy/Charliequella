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

package com.tle.web.api.newuitheme.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class NewUITheme {

  private String primaryColor = "#186caf";
  private String secondaryColor = "#ff9800";
  private String backgroundColor = "#fafafa";
  private String menuItemColor = "#ffffff";
  private String menuItemIconColor = "#000000";
  private String menuItemTextColor = "#000000";
  private String primaryTextColor = "#000000";
  private String menuTextColor = "#444444";
  private int fontSize = 14;

  public String getPrimaryColor() {
    return primaryColor;
  }

  public void setPrimaryColor(String primaryColor) {
    this.primaryColor = primaryColor;
  }

  public String getSecondaryColor() {
    return secondaryColor;
  }

  public void setSecondaryColor(String secondaryColor) {
    this.secondaryColor = secondaryColor;
  }

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public String getMenuItemColor() {
    return menuItemColor;
  }

  public void setMenuItemColor(String menuItemColor) {
    this.menuItemColor = menuItemColor;
  }

  public String getMenuItemTextColor() {
    return menuItemTextColor;
  }

  public void setMenuItemTextColor(String menuItemTextColor) {
    this.menuItemTextColor = menuItemTextColor;
  }

  public int getFontSize() {
    return fontSize;
  }

  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }

  public String getMenuItemIconColor() {
    return menuItemIconColor;
  }

  public void setMenuItemIconColor(String menuItemIconColor) {
    this.menuItemIconColor = menuItemIconColor;
  }

  public String getPrimaryTextColor() {
    return primaryTextColor;
  }

  public void setPrimaryTextColor(String primaryTextColor) {
    this.primaryTextColor = primaryTextColor;
  }

  public String getMenuTextColor() {
    return menuTextColor;
  }

  public void setMenuTextColor(String menuTextColor) {
    this.menuTextColor = menuTextColor;
  }

  public String toSassVars() {
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, ?> themeVars = objectMapper.convertValue(this, Map.class);
    StringBuilder sassTheme = new StringBuilder();

    themeVars.forEach(
        (key, value) -> {
          sassTheme.append("$" + key + " : " + value + ";\r\n");
        });

    return sassTheme.toString();
  }
}
