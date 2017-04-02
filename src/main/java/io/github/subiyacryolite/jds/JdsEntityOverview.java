/*
* Jenesis Data Store Copyright (c) 2017 Ifunga Ndana. All rights reserved.
*
* 1. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
*
* 2. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
*
* 3. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
*
* Neither the name Jenesis Data Store nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package io.github.subiyacryolite.jds;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by ifunga on 14/02/2017.
 */
public class JdsEntityOverview {
    private final SimpleObjectProperty<LocalDateTime> dateCreated;
    private final SimpleObjectProperty<LocalDateTime> dateModified;
    private final SimpleLongProperty serviceCode;
    private final SimpleStringProperty entityGuid;

    JdsEntityOverview() {
        this.entityGuid = new SimpleStringProperty(UUID.randomUUID().toString());
        this.dateCreated = new SimpleObjectProperty<>(LocalDateTime.now());
        this.dateModified = new SimpleObjectProperty<>(LocalDateTime.now());
        this.serviceCode = new SimpleLongProperty();
    }

    public LocalDateTime getDateCreated() {
        return dateCreated.get();
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated.set(dateCreated);
    }

    public LocalDateTime getDateModified() {
        return dateModified.get();
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified.set(dateModified);
    }

    public long getEntityCode() {
        return serviceCode.get();
    }

    public void setEntityCode(long serviceCode) {
        this.serviceCode.set(serviceCode);
    }

    public String getEntityGuid() {
        return entityGuid.get();
    }

    public void setEntityGuid(String entityGuid) {
        this.entityGuid.set(entityGuid);
    }
}
