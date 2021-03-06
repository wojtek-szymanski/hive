/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.druid.json;

import io.druid.segment.IndexSpec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.joda.time.Duration;
import org.joda.time.Period;

import java.io.File;

/**
 * This class is copied from druid source code
 * in order to avoid adding additional dependencies on druid-indexing-service.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({@JsonSubTypes.Type(
    name = "kafka",
    value = KafkaSupervisorTuningConfig.class
)})
public class KafkaSupervisorTuningConfig extends KafkaTuningConfig
{
  private final Integer workerThreads;
  private final Integer chatThreads;
  private final Long chatRetries;
  private final Duration httpTimeout;
  private final Duration shutdownTimeout;
  private final Duration offsetFetchPeriod;

  public KafkaSupervisorTuningConfig(
      @JsonProperty("maxRowsInMemory") Integer maxRowsInMemory,
      @JsonProperty("maxRowsPerSegment") Integer maxRowsPerSegment,
      @JsonProperty("intermediatePersistPeriod") Period intermediatePersistPeriod,
      @JsonProperty("basePersistDirectory") File basePersistDirectory,
      @JsonProperty("maxPendingPersists") Integer maxPendingPersists,
      @JsonProperty("indexSpec") IndexSpec indexSpec,
      // This parameter is left for compatibility when reading existing configs, to be removed in Druid 0.12.
      @JsonProperty("buildV9Directly") Boolean buildV9Directly,
      @JsonProperty("reportParseExceptions") Boolean reportParseExceptions,
      @JsonProperty("handoffConditionTimeout") Long handoffConditionTimeout, // for backward compatibility
      @JsonProperty("resetOffsetAutomatically") Boolean resetOffsetAutomatically,
      @JsonProperty("workerThreads") Integer workerThreads,
      @JsonProperty("chatThreads") Integer chatThreads,
      @JsonProperty("chatRetries") Long chatRetries,
      @JsonProperty("httpTimeout") Period httpTimeout,
      @JsonProperty("shutdownTimeout") Period shutdownTimeout,
      @JsonProperty("offsetFetchPeriod") Period offsetFetchPeriod
  )
  {
    super(
        maxRowsInMemory,
        maxRowsPerSegment,
        intermediatePersistPeriod,
        basePersistDirectory,
        maxPendingPersists,
        indexSpec,
        true,
        reportParseExceptions,
        // Supervised kafka tasks should respect KafkaSupervisorIOConfig.completionTimeout instead of
        // handoffConditionTimeout
        handoffConditionTimeout,
        resetOffsetAutomatically
    );

    this.workerThreads = workerThreads;
    this.chatThreads = chatThreads;
    this.chatRetries = (chatRetries != null ? chatRetries : 8);
    this.httpTimeout = defaultDuration(httpTimeout, "PT10S");
    this.shutdownTimeout = defaultDuration(shutdownTimeout, "PT80S");
    this.offsetFetchPeriod = defaultDuration(offsetFetchPeriod, "PT30S");
  }

  @JsonProperty
  public Integer getWorkerThreads()
  {
    return workerThreads;
  }

  @JsonProperty
  public Integer getChatThreads()
  {
    return chatThreads;
  }

  @JsonProperty
  public Long getChatRetries()
  {
    return chatRetries;
  }

  @JsonProperty
  public Duration getHttpTimeout()
  {
    return httpTimeout;
  }

  @JsonProperty
  public Duration getShutdownTimeout()
  {
    return shutdownTimeout;
  }

  @JsonProperty
  public Duration getOffsetFetchPeriod()
  {
    return offsetFetchPeriod;
  }

  @Override
  public String toString()
  {
    return "KafkaSupervisorTuningConfig{" +
        "maxRowsInMemory=" + getMaxRowsInMemory() +
        ", maxRowsPerSegment=" + getMaxRowsPerSegment() +
        ", intermediatePersistPeriod=" + getIntermediatePersistPeriod() +
        ", basePersistDirectory=" + getBasePersistDirectory() +
        ", maxPendingPersists=" + getMaxPendingPersists() +
        ", indexSpec=" + getIndexSpec() +
        ", reportParseExceptions=" + isReportParseExceptions() +
        ", handoffConditionTimeout=" + getHandoffConditionTimeout() +
        ", resetOffsetAutomatically=" + isResetOffsetAutomatically() +
        ", workerThreads=" + workerThreads +
        ", chatThreads=" + chatThreads +
        ", chatRetries=" + chatRetries +
        ", httpTimeout=" + httpTimeout +
        ", shutdownTimeout=" + shutdownTimeout +
        ", offsetFetchPeriod=" + offsetFetchPeriod +
        '}';
  }

  private static Duration defaultDuration(final Period period, final String theDefault)
  {
    return (period == null ? new Period(theDefault) : period).toStandardDuration();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;

    KafkaSupervisorTuningConfig that = (KafkaSupervisorTuningConfig) o;

    if (workerThreads != null ?
        !workerThreads.equals(that.workerThreads) :
        that.workerThreads != null)
      return false;
    if (chatThreads != null ? !chatThreads.equals(that.chatThreads) : that.chatThreads != null)
      return false;
    if (chatRetries != null ? !chatRetries.equals(that.chatRetries) : that.chatRetries != null)
      return false;
    if (httpTimeout != null ? !httpTimeout.equals(that.httpTimeout) : that.httpTimeout != null)
      return false;
    if (shutdownTimeout != null ?
        !shutdownTimeout.equals(that.shutdownTimeout) :
        that.shutdownTimeout != null)
      return false;
    return offsetFetchPeriod != null ?
        offsetFetchPeriod.equals(that.offsetFetchPeriod) :
        that.offsetFetchPeriod == null;
  }
  
  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (workerThreads != null ? workerThreads.hashCode() : 0);
    result = 31 * result + (chatThreads != null ? chatThreads.hashCode() : 0);
    result = 31 * result + (chatRetries != null ? chatRetries.hashCode() : 0);
    result = 31 * result + (httpTimeout != null ? httpTimeout.hashCode() : 0);
    result = 31 * result + (shutdownTimeout != null ? shutdownTimeout.hashCode() : 0);
    result = 31 * result + (offsetFetchPeriod != null ? offsetFetchPeriod.hashCode() : 0);
    return result;
  }
}
