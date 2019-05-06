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

package com.tle.common.scripting.types;

import java.io.Serializable;
import java.util.List;

/**
 * The Attachment object type as used by scripts. This is an abstraction away from the real
 * Attachment object used internally on the server.
 */
public interface AttachmentScriptType extends Serializable {
  /**
   * Get the path to the physical file, relative to the item it is attached to. E.g.
   * 'attachments/myfile.jpg' Note that if you invoke this method on a Link attachment, the URL will
   * be returned.
   *
   * @return The path to the physical file, relative to the item.
   */
  String getFilename();

  /**
   * Get the URL for a Link attachment E.g. 'http://www.google.com.au' Note that if you invoke this
   * method on a File attachment, the path to the file will be returned as in getFilename().
   *
   * @return The URL of the link attachment.
   */
  String getUrl();

  /**
   * Sets the URL for a Link attachment. Do not call this method on a File attachment as the path to
   * the file will be overwritten by the URL.
   *
   * @param url
   */
  void setUrl(String url);

  /** @return The UUID of the attachment, generated by the system. */
  String getUuid();

  /** @return The text to display as a link to this attachment */
  String getDescription();

  /** @param description The text to display as a link to this attachment. E.g. 'My Text File' */
  void setDescription(String description);

  /**
   * The type of the attachment. The most common types will be FILE, LINK, ZIP and IMS
   *
   * @return FILE, ITEM, ZIP, LINK, IMS, ACTIVITY, IMSRES, CUSTOM or HTML
   */
  String getType();

  /**
   * Size in bytes for physical files, or zero for an attachment type with no physical file (E.g. a
   * Link attachment).
   *
   * @return The file size in bytes.
   */
  long getSize();

  /**
   * The path to the thumbnail image file. This may be a system thumbnail (based on the MIME type of
   * the attachment) If this attachment is an image, a thumbnail of the image will be generated and
   * this method will return the path to that thumbnail.
   *
   * @return The path to this attachment's thumbnail
   */
  String getThumbnail();

  /**
   * Sets the path to the thumbnail for the attachment. The path should be a relative path from the
   * item's filestore location. Use 'suppress' as the path to stop the automatic generation of
   * thumbnails for image attachments.
   */
  void setThumbnail(String path);

  /**
   * The type of this custom attachment, for example, "flickr". This will throw an error unless
   * <code>getType()</code> returns <code>CUSTOM</code>.
   *
   * @return The type of this custom attachment.
   */
  String getCustomType();

  /**
   * For attachments of type CUSTOM, get the value of the given property (if any). If the attachment
   * is not of type CUSTOM an exception will be thrown.
   *
   * @param propertyName The name of the custom property.
   * @return The value of the property, or null if not found.
   */
  Object getCustomProperty(String propertyName);

  /**
   * For attachments of type CUSTOM, set the value of the given property. If the attachment is not
   * of type CUSTOM an exception will be thrown. Due to all numbers being represented as "double"
   * (decimal point numbers) in Javascript, you should use the setCustomIntegerProperty(String, int)
   * method if you need to the values of integer properties, otherwise your property will become a
   * decimal type.
   *
   * @param propertyName The name of the custom property.
   * @param propertyValue The new value of the custom property.
   */
  void setCustomProperty(String propertyName, Object propertyValue);

  /**
   * For attachments of type CUSTOM, set the value of the given property. If the attachment is not
   * of type CUSTOM an exception will be thrown.
   *
   * @param propertyName The name of the custom property.
   * @param propertyValue The new integer (whole number) value of the custom property, e.g. 3
   */
  void setCustomIntegerProperty(String propertyName, int propertyValue);

  /**
   * For attachments of type CUSTOM, set the value of the given property. If the attachment is not
   * of type CUSTOM an exception will be thrown.
   *
   * @param propertyName The name of the custom property.
   * @param propertyValue The new long (whole number) value of the custom property, e.g. 6666666666
   */
  void setCustomLongProperty(String propertyName, long propertyValue);

  /**
   * For attachments of type CUSTOM, set the value of the given property. If the attachment is not
   * of type CUSTOM an exception will be thrown.
   *
   * @param propertyName The name of the custom property.
   * @param propertyValue The new double value of the custom property, e.g. 66.66
   */
  void setCustomDoubleProperty(String propertyName, double propertyValue);

  /**
   * Used to display custom metadata on the attachments dropdown. All display properties will be
   * ordered as they are inserted during scripting. If value is null the custom field will be
   * removed. The key makes up the label that will be displayed in the user interface so it should
   * be a human readable string for example: setCustomDisplayProperty('Author', 'John Doe'); will
   * create the following on an attachment 'Author: John Doe'
   *
   * @param key The name of the custom property
   * @param value The value of the custom property
   */
  void setCustomDisplayProperty(String key, Object value);

  /**
   * Gets a specific custom display property or null if it does not exist
   *
   * @param key The key of the custom property
   * @return The value of the property
   */
  Object getCustomDisplayProperty(String key);

  /**
   * Returns a list of all the keys used for custom metadata display
   *
   * @return a list of all keys
   */
  List<String> getAllCustomDisplayProperties();

  /**
   * @return The number of views this attachment has had, proided the current user has permissions.
   *     Otherwise will return null.
   */
  Integer getViewCount();
}
