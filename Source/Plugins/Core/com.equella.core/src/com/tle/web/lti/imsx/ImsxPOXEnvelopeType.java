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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2013.07.10 at 02:09:22 PM EST
//

package com.tle.web.lti.imsx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Java class for imsx_POXEnvelope.Type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="imsx_POXEnvelope.Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0}imsx_POXHeader"/>
 *         &lt;element ref="{http://www.imsglobal.org/services/ltiv1p1/xsd/imsoms_v1p0}imsx_POXBody"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "imsx_POXEnvelope.Type",
    propOrder = {"imsxPOXHeader", "imsxPOXBody"})
public class ImsxPOXEnvelopeType {

  @XmlElement(name = "imsx_POXHeader", required = true)
  protected ImsxPOXHeaderType imsxPOXHeader;

  @XmlElement(name = "imsx_POXBody", required = true)
  protected ImsxPOXBodyType imsxPOXBody;

  /**
   * Gets the value of the imsxPOXHeader property.
   *
   * @return possible object is {@link ImsxPOXHeaderType }
   */
  public ImsxPOXHeaderType getImsxPOXHeader() {
    return imsxPOXHeader;
  }

  /**
   * Sets the value of the imsxPOXHeader property.
   *
   * @param value allowed object is {@link ImsxPOXHeaderType }
   */
  public void setImsxPOXHeader(ImsxPOXHeaderType value) {
    this.imsxPOXHeader = value;
  }

  /**
   * Gets the value of the imsxPOXBody property.
   *
   * @return possible object is {@link ImsxPOXBodyType }
   */
  public ImsxPOXBodyType getImsxPOXBody() {
    return imsxPOXBody;
  }

  /**
   * Sets the value of the imsxPOXBody property.
   *
   * @param value allowed object is {@link ImsxPOXBodyType }
   */
  public void setImsxPOXBody(ImsxPOXBodyType value) {
    this.imsxPOXBody = value;
  }
}
