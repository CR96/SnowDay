/*
 * Copyright 2014 - 2018 Corey Rowe
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

package com.gbsnowday.snowday.model;

/**
 * An object containing the status of a single school or organization.
 */
public class ClosingModel {
    private final String orgName;
    private final String orgStatus;
    private boolean closed;

    private ClosingModel(ClosingBuilder builder) {
        orgName = builder.orgName;
        orgStatus = builder.orgStatus;
        closed = builder.closed;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getOrgStatus() {
        return orgStatus;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    // Builder pattern used to avoid ambiguous constructors
    public static class ClosingBuilder {
        //Required
        private final String orgName;

        //Optional
        private String orgStatus = "";
        private final boolean closed = false;

        public ClosingBuilder(String orgName) {
            this.orgName = orgName;
        }

        public ClosingBuilder setOrgStatus(String s) {
            orgStatus = s;
            return this;
        }

        public ClosingModel build() {
            return new ClosingModel(this);
        }
    }
}