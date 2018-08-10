package com.gbsnowday.snowday.model;

/*Copyright 2014-2016 Corey Rowe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

/**
 * An object containing the status of a single school or organization.
 */
public class ClosingModel {
    private String orgName;
    private String orgStatus;
    private boolean sectionHeader;
    private boolean messagePresent;
    private boolean closed;

    public String getOrgName() {
        return orgName;
    }

    public String getOrgStatus() {
        return orgStatus;
    }

    public boolean isSectionHeader() {
        return sectionHeader;
    }

    public boolean isMessagePresent() {
        return messagePresent;
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
        private boolean sectionHeader = false;
        private boolean messagePresent = false;
        private boolean closed = false;

        public ClosingBuilder(String orgName) {
            this.orgName = orgName;
        }

        public ClosingBuilder setOrgStatus(String s) {
            orgStatus = s;
            return this;
        }

        public ClosingBuilder setSectionHeader(boolean b) {
            sectionHeader = b;
            return this;
        }

        public ClosingBuilder setMessagePresent(boolean b) {
            messagePresent = b;
            return this;
        }

        public ClosingBuilder setClosed(boolean b) {
            closed = b;
            return this;
        }

        public ClosingModel build() {
            return new ClosingModel(this);
        }
    }

    private ClosingModel (ClosingBuilder builder) {
        orgName = builder.orgName;
        orgStatus = builder.orgStatus;
        sectionHeader = builder.sectionHeader;
        messagePresent = builder.messagePresent;
        closed = builder.closed;
    }
}