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

package com.tle.integration.lti.blackboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.tle.annotation.NonNullByDefault;
import com.tle.annotation.Nullable;
import com.tle.beans.item.IItem;
import com.tle.beans.item.ItemId;
import com.tle.beans.item.ViewableItemType;
import com.tle.beans.item.attachments.IAttachment;
import com.tle.common.Check;
import com.tle.common.NameValue;
import com.tle.common.connectors.ConnectorFolder;
import com.tle.common.connectors.entity.Connector;
import com.tle.common.usermanagement.user.CurrentUser;
import com.tle.common.usermanagement.user.UserState;
import com.tle.core.connectors.blackboard.service.BlackboardConnectorService;
import com.tle.core.connectors.service.ConnectorRepositoryService;
import com.tle.core.connectors.service.ConnectorService;
import com.tle.core.guice.Bind;
import com.tle.core.replicatedcache.ReplicatedCacheService;
import com.tle.web.integration.AbstractIntegrationService;
import com.tle.web.integration.IntegrationActionInfo;
import com.tle.web.integration.SingleSignonForm;
import com.tle.web.integration.guice.IntegrationModule;
import com.tle.web.integration.service.IntegrationService;
import com.tle.web.lti.LtiData;
import com.tle.web.lti.usermanagement.LtiUserState;
import com.tle.web.sections.SectionInfo;
import com.tle.web.sections.equella.annotation.PlugKey;
import com.tle.web.sections.equella.annotation.PluginResourceHandler;
import com.tle.web.sections.equella.receipt.ReceiptService;
import com.tle.web.sections.render.Label;
import com.tle.web.sections.result.util.KeyLabel;
import com.tle.web.sections.result.util.PluralKeyLabel;
import com.tle.web.selection.SelectedResource;
import com.tle.web.selection.SelectedResourceKey;
import com.tle.web.selection.SelectionSession;
import com.tle.web.selection.section.RootSelectionSection;
import com.tle.web.viewable.ViewableItem;
import com.tle.web.viewable.ViewableItemResolver;
import com.tle.web.viewurl.ViewableResource;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.log4j.Logger;

@Bind
@Singleton
@NonNullByDefault
@SuppressWarnings("nls")
public class BlackboardLtiIntegration extends AbstractIntegrationService<BlackboardLtiSessionData> {
  private static final Logger LOGGER = Logger.getLogger(BlackboardLtiIntegration.class);

  // TODO - I think all this can be removed.
  // Omitted logic for now...
  //
  // private static final String CUSTOM_CANVAS_COURSE_ID = "custom_bb_course_id";
  // private static final String CUSTOM_CANVAS_API_DOMAIN = "custom_bb_api_domain";
  // private static final String LIS_COURSE_OFFERING_SOURCEDID = "lis_course_offering_sourcedid";
  private static final String CONTENT_ITEM_SELECTION_REQUEST = "ContentItemSelectionRequest";

  // TODO - I think all this can be removed.
  // Omitted logic for now...
  //
  // These two are only supplied to us if configured in the Canvas LTI tool setup.  E.g.:
  // Custom Fields:
  // course_id=$Canvas.course.sisSourceId
  // course_code=$com.instructure.contextLabel
  // private static final String CUSTOM_COURSE_ID = "custom_course_id";
  // private static final String CUSTOM_COURSE_CODE = "custom_course_code";

  static {
    PluginResourceHandler.init(BlackboardLtiIntegration.class);
  }

  @PlugKey("integration.receipt.addedtoblackboard")
  private static String KEY_RECEIPT_ADDED;

  @PlugKey("canvas.error.requireoneconnector")
  private static String KEY_ERROR_NO_SINGLE_CONNECTOR;

  @PlugKey("integration.error.nocourse")
  private static Label LABEL_ERROR_NO_COURSE;

  @PlugKey("integration.error.noapidomain")
  private static Label LABEL_ERROR_NO_API_DOMAIN;

  @Inject private IntegrationService integrationService;
  @Inject private ViewableItemResolver viewableItemResolver;
  @Inject private ReceiptService receiptService;
  @Inject private ConnectorService connectorService;
  @Inject private BlackboardConnectorService bbService;
  @Inject private ConnectorRepositoryService connectorRepoService;
  // Omitted logic for now...
  //
  @Inject private ReplicatedCacheService cacheService;

  private ReplicatedCacheService.ReplicatedCache<String> courseStructureCache;
  private final ObjectMapper objectMapper = new ObjectMapper();

  // TODO - I think all this can be removed.
  //	private static final Multimap<String, String> CONTENT_TYPES_TO_MIME = HashMultimap.create(6,
  // 6);
  //
  //	static
  //	{
  //		// oembed,lti_launch_url,url,image_url,iframe
  //		// CONTENT_TYPES_TO_MIME.put("oembed", "");
  //		CONTENT_TYPES_TO_MIME.put("image_url", "image/jpeg");
  //		CONTENT_TYPES_TO_MIME.put("image_url", "image/gif");
  //		CONTENT_TYPES_TO_MIME.put("image_url", "image/png");
  //	}
  // Omitted logic for now...
  //
  //	@PostConstruct
  //	public void setupCache()
  //	{
  //		courseStructureCache = cacheService.getCache("BlackboardSignonCourseStructure", 100, 2,
  // TimeUnit.MINUTES);
  //	}

  @Override
  protected String getIntegrationType() {
    return "blackboardLti";
  }

  public boolean isItemOnly(BlackboardLtiSessionData data) {
    return false;
  }

  @Override
  public BlackboardLtiSessionData createDataForViewing(SectionInfo info) {
    return new BlackboardLtiSessionData();
  }

  @Override
  public void setupSingleSignOn(SectionInfo info, SingleSignonForm form) {
    final BlackboardLtiSessionData data = new BlackboardLtiSessionData(info.getRequest());
    String courseId = null;
    final UserState userState = CurrentUser.getUserState();
    // TODO:  I think this can be removed as well
    // final List<String> courseCodes = new ArrayList<String>();
    String courseCode = form.getCourseCode();
    if (userState instanceof LtiUserState) {
      final LtiUserState ltiUserState = (LtiUserState) userState;
      final LtiData ltiData = ltiUserState.getData();
      if (ltiData != null) {
        // TODO: does BB pass anything about this?  Want a second opinion, but I don't think so.
        data.setApiDomain(null); // ltiData.getCustom(CUSTOM_CANVAS_API_DOMAIN));
        courseId = form.getCourseId();
        if (Strings.isNullOrEmpty(courseId)) {
          // courseId = ltiData.getCustom(CUSTOM_CANVAS_COURSE_ID);
          courseId = ltiData.getContextId();
        }
        data.setCourseId(courseId);
        data.setContextTitle(ltiData.getContextTitle());
        //
        //				courseCodes.add(ltiData.getCustom(CUSTOM_COURSE_CODE));
        //				courseCodes.add(ltiData.getCustom(CUSTOM_COURSE_ID));
        //				courseCodes.add(ltiData.getCustom(LIS_COURSE_OFFERING_SOURCEDID));
        //				courseCodes.add(ltiData.getContextLabel());

        if (Strings.isNullOrEmpty(courseCode)) {
          courseCode = ltiData.getContextLabel();
        }
      }
    }

    data.setCourseInfoCode(integrationService.getCourseInfoCode(courseId, courseCode));

    String formDataAction = form.getAction();
    if (formDataAction == null) {
      formDataAction = IntegrationModule.SELECT_OR_ADD_DEFAULT_ACTION;
    }

    IntegrationActionInfo actionInfo =
        integrationService.getActionInfo(formDataAction, form.getOptions());
    if (actionInfo.getName().equals("unknown")) {
      actionInfo = integrationService.getActionInfoForUrl('/' + data.getAction());
    }
    if (actionInfo == null) {
      actionInfo = new IntegrationActionInfo();
    }

    LOGGER.debug("BlackboardLtiIntegration.setupSingleSignOn: about to forward - data=" + data);
    integrationService.standardForward(
        info, convertToForward(actionInfo, form), data, actionInfo, form);
  }

  private String convertToForward(IntegrationActionInfo action, SingleSignonForm model) {
    String forward = action.getPath();
    if (forward == null) {
      forward = action.getName();
    }

    if (action.getName().equals("standard")) {
      forward = forward + model.getQuery();
    }

    return forward.substring(1);
  }

  @Nullable
  @Override
  public SelectionSession setupSelectionSession(
      SectionInfo info,
      BlackboardLtiSessionData data,
      SelectionSession session,
      SingleSignonForm form) {
    final boolean structured = "structured".equals(data.getAction());

    session.setSelectMultiple(true); // all integration points support multiple
    session.setAttachmentUuidUrls(true); // Always
    session.setInitialItemXml(form.getItemXml());
    session.setInitialPowerXml(form.getPowerXml());
    session.setCancelDisabled(form.isCancelDisabled());

    // Setup the structure param before super.setupSelectionSession so the
    // extension can setup the TargetStructure
    if (structured) {
      form.setStructure(initStructure(data, session, form));
    }

    return super.setupSelectionSession(info, data, session, form);
  }

  @Nullable
  private String initStructure(
      BlackboardLtiSessionData data, SelectionSession session, SingleSignonForm form) {
    final String courseId = data.getCourseId();
    String structure = form.getStructure();
    if (structure == null) {
      // if course ID is empty then there is nothing we can do...
      if (Strings.isNullOrEmpty(courseId)) {
        throw new RuntimeException(LABEL_ERROR_NO_COURSE.getText());
      }
      structure = courseStructureCache.get(courseId).orNull();
    }
    // if no structure, get from Canvas
    if (structure == null) {
      final ObjectNode root = objectMapper.createObjectNode();
      root.put("id", courseId);
      root.put("name", data.getContextTitle());
      root.put("targetable", false);
      final ArrayNode foldersNode = objectMapper.createArrayNode();
      root.put("folders", foldersNode);

      final Connector connector = findConnector(data);
      final List<ConnectorFolder> folders =
          connectorRepoService.getFoldersForCourse(
              connector, CurrentUser.getUsername(), courseId, false);
      boolean first = true;
      for (ConnectorFolder folder : folders) {
        final ObjectNode folderNode = objectMapper.createObjectNode();
        folderNode.put("id", folder.getId());
        folderNode.put("name", folder.getName());
        folderNode.put("targetable", true);
        folderNode.put("defaultFolder", first);
        foldersNode.add(folderNode);
        first = false;
      }

      final PrettyPrinter pp = new MinimalPrettyPrinter();
      try {
        structure = objectMapper.writer().with(pp).writeValueAsString(root);
      } catch (JsonProcessingException e) {
        throw Throwables.propagate(e);
      }
    }
    if (structure != null) {
      courseStructureCache.put(courseId, structure);
    }
    return structure;
  }

  private Connector findConnector(BlackboardLtiSessionData data) {
    Connector connector = null;
    final String connectorUuid = data.getConnectorUuid();
    if (connectorUuid != null) {
      connector = connectorService.getByUuid(connectorUuid);
    }
    if (connector == null) {
      final String apiDomain = data.getApiDomain();
      if (apiDomain != null) {
        final String tcUrl = "://" + apiDomain;

        final List<Connector> connectors = connectorService.enumerateForUrl(tcUrl);
        if (connectors.size() == 1) {
          connector = connectors.get(0);
          data.setConnectorUuid(connector.getUuid());
        } else {
          throw new RuntimeException(
              new KeyLabel(KEY_ERROR_NO_SINGLE_CONNECTOR, connectors.size(), tcUrl).getText());
        }
      } else {
        throw new RuntimeException(LABEL_ERROR_NO_API_DOMAIN.getText());
      }
    }
    return connector;
  }

  @Override
  public boolean select(SectionInfo info, BlackboardLtiSessionData data, SelectionSession session) {
    try {
      // TODO: a find a better way to determine if in structured session or not
      if (!session.getLayout().equals(RootSelectionSection.Layout.COURSE)) {
        String lti_message_type = data.getLtiMessageType();
        if (lti_message_type != null
            && lti_message_type.equalsIgnoreCase(CONTENT_ITEM_SELECTION_REQUEST)) {
          info.forward(info.createForward("/blackboardlticipreturn.do"));
        } else {
          final SelectedResource resource = getFirstSelectedResource(session);
          final IItem<?> item = getItemForResource(resource);

          final LmsLink link =
              getLinkForResource(
                      info,
                      createViewableItem(item, resource),
                      resource,
                      false,
                      session.isAttachmentUuidUrls())
                  .getLmsLink();

          final String mimeType;
          if (!Check.isEmpty(resource.getAttachmentUuid())) {
            final SelectedResourceKey key = resource.getKey();
            final ViewableItem<?> viewableItem =
                viewableItemResolver.createViewableItem(item, key.getExtensionType());
            final IAttachment attachment =
                viewableItem.getAttachmentByUuid(resource.getAttachmentUuid());
            final ViewableResource viewableResource =
                attachmentResourceService.getViewableResource(info, viewableItem, attachment);
            mimeType = viewableResource.getMimeType();
          } else {
            mimeType = "equella/item";
          }
          final String launchPresentationReturnUrl = data.getLaunchPresentationReturnUrl();
          final StringBuilder retUrl = new StringBuilder(launchPresentationReturnUrl);
          retUrl.append(launchPresentationReturnUrl.contains("?") ? "&" : "?");

          //					final Set<String> extContentReturnTypes = data.getExtContentReturnTypes();
          //
          //					if( extContentReturnTypes.contains("image_url")
          //						&& CONTENT_TYPES_TO_MIME.get("image_url").contains(mimeType) )
          //					{
          //						retUrl.append("return_type=image_url");
          //
          //						retUrl.append("&url=");
          //						retUrl.append(URLEncoder.encode(link.getUrl(), "UTF-8"));
          //
          //						retUrl.append("&alt=");
          //						retUrl.append(URLEncoder.encode(link.getName(), "UTF-8"));
          //					}
          // "oembed" not supported yet
          // "iframe"
          // "url"

          //					else
          //					{
          retUrl.append("return_type=lti_launch_url");

          retUrl.append("&url=");
          retUrl.append(URLEncoder.encode(link.getUrl(), "UTF-8"));

          // e.g. <a title="${title}"></a>
          retUrl.append("&title=");
          retUrl.append(URLEncoder.encode(link.getName(), "UTF-8"));

          // e.g. <a>${text}</a>
          retUrl.append("&text=");
          retUrl.append(URLEncoder.encode(link.getName(), "UTF-8"));
          //					}

          info.forwardToUrl(retUrl.toString());
        }

        // maintain the selections so they survive the forwarding
        return true;

      } else // TODO - I don't think there is a way to select multiple resources with the current Bb
      // UI.
      {
        final Connector connector = findConnector(data);

        final String courseId = session.getStructure().getId();

        // add resources via REST
        final Collection<SelectedResource> selectedResources = session.getSelectedResources();
        for (SelectedResource resource : selectedResources) {
          final IItem<?> item = getItemForResource(resource);
          final String moduleId = resource.getKey().getFolderId();
          //					TODO impl
          //					bbService.addItemToCourse(connector, CurrentUser.getUsername(), courseId, moduleId,
          // item,
          //						resource);

        }
        final int count = selectedResources.size();
        // clear session
        session.clearResources();

        // provide receipt and stay where we are
        receiptService.setReceipt(new PluralKeyLabel(KEY_RECEIPT_ADDED, count));
      }

      return false;
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public String getClose(BlackboardLtiSessionData data) {
    return data.getLaunchPresentationReturnUrl();
  }

  @Nullable
  @Override
  public String getCourseInfoCode(BlackboardLtiSessionData data) {
    return data.getCourseInfoCode();
  }

  @Nullable
  @Override
  public NameValue getLocation(BlackboardLtiSessionData data) {
    return null;
  }

  @Override
  protected boolean canSelect(BlackboardLtiSessionData data) {
    // can be select_link, embed_content etc
    final String sd = data.getSelectionDirective();
    return sd != null || "ContentItemSelectionRequest".equals(data.getLtiMessageType());
  }

  @Override
  protected <I extends IItem<?>> ViewableItem<I> createViewableItem(
      I item, SelectedResource resource) {
    final ViewableItem<I> vitem =
        viewableItemResolver.createIntegrationViewableItem(
            item,
            resource.isLatest(),
            ViewableItemType.GENERIC,
            resource.getKey().getExtensionType());
    return vitem;
  }

  @Override
  public <I extends IItem<?>> ViewableItem<I> createViewableItem(
      ItemId itemId, boolean latest, @Nullable String itemExtensionType) {
    final ViewableItem<I> vitem =
        viewableItemResolver.createIntegrationViewableItem(
            itemId, latest, ViewableItemType.GENERIC, itemExtensionType);
    return vitem;
  }
}
