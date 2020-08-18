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
import { render, queryByTestId } from "@testing-library/react";
import * as React from "react";
import { getSearchResult } from "../../../../__mocks__/getSearchResult";
import SearchResult from "../../../../tsrc/search/components/SearchResult";

import "@testing-library/jest-dom/extend-expect";
import { BrowserRouter } from "react-router-dom";

describe("<SearchResult/>", () => {
  const renderSearchResult = (itemResult) => {
    const item = itemResult;
    return render(
      //This needs to be wrapped inside a BrowserRouter, to prevent an `Invariant failed: You should not use <Link> outside a <Router>` error
      <BrowserRouter>
        <SearchResult {...item} key={item.uuid} />
      </BrowserRouter>
    );
  };

  it("Should show indicator in Attachment panel if keyword was found inside attachment", () => {
    const itemWithSearchTermFound = getSearchResult.results[0];
    const { container } = renderSearchResult(itemWithSearchTermFound);
    expect(
      queryByTestId(container, "keywordFoundInAttachment")
    ).toBeInTheDocument();
  });

  it("Should not show indicator in Attachment panel if keyword was found inside attachment", () => {
    const itemWithNoSearchTermFound = getSearchResult.results[1];
    const { container } = renderSearchResult(itemWithNoSearchTermFound);
    expect(
      queryByTestId(container, "keywordFoundInAttachment")
    ).not.toBeInTheDocument();
  });
});
