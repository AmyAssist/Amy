/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package de.unistuttgart.iaas.amyassist.amy.remotesr;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import org.slf4j.Logger;

/**
 * Class to initiate the remote SR
 * @author Benno Krauß
 */
@Service
public class RemoteSR {

    @Reference
    private Logger logger;

    private static final String START_SR_EVENT = "START";

    private SSEClient client = null;

    private RemoteSRListener listener;

    void setClient(SSEClient client) {
        if (this.client != null && this.client.isConnected()) {
            logger.warn("New SSE client connected before the old one disconnected");
            this.client.disconnect();
        }
        this.client = client;
    }

    /**
     * request remote SR
     * @return true if the request was successful
     */
    public boolean requestSR() {
        return client != null && client.sendEvent(START_SR_EVENT);
    }

    void processResult(SRResult res) {
        if (res != null && res.getText() != null && !res.getText().isEmpty() && listener != null) {
            listener.remoteSRDidRecognizeSpeech(res.getText());
        }
    }

    public void setListener(RemoteSRListener listener) {
        this.listener = listener;
    }
}
