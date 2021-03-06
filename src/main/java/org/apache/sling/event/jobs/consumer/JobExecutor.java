/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.event.jobs.consumer;

import org.apache.sling.event.jobs.Job;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * A job executor consumes a job.
 * <p>
 * A job executor registers itself with the {@link #PROPERTY_TOPICS} service registration
 * property. The value of this property defines which topics an executor is able to process.
 * Each string value of this property is either
 * <ul>
 * <li>a job topic, or
 * <li>a topic category ending with "/*" which means all topics in this category, or
 * <li>a topic category ending with "/**" which means all topics in this category and all
 *     sub categories. This matching is new since version 1.2.
 * </ul>
 * A consumer registering for just "*" or "**" is not considered.
 * <p>
 * For example, the value {@code org/apache/sling/jobs/*} matches the topics
 * {@code org/apache/sling/jobs/a} and {@code org/apache/sling/jobs/b} but neither
 * {@code org/apache/sling/jobs} nor {@code org/apache/sling/jobs/subcategory/a}. A value of
 * {@code org/apache/sling/jobs/**} matches the same topics but also all sub topics
 * like {@code org/apache/sling/jobs/subcategory/a} or {@code org/apache/sling/jobs/subcategory/a/c/d}.
 * <p>
 * If there is more than one job consumer or executor registered for a job topic, the selection is as
 * follows:
 * <ul>
 * <li>If there is a single consumer registering for the exact topic, this one is used.
 * <li>If there is more than a single consumer registering for the exact topic, the one
 *     with the highest service ranking is used. If the ranking is equal, the one with
 *     the lowest service ID is used.
 * <li>If there is a single consumer registered for the category, it is used.
 * <li>If there is more than a single consumer registered for the category, the service
 *     with the highest service ranking is used. If the ranking is equal, the one with
 *     the lowest service ID is used.
 * <li>The search continues with consumer registered for deep categories. The nearest one
 *     is tried next. If there are several, the one with the highest service ranking is
 *     used. If the ranking is equal, the one with the lowest service ID is used.
 * </ul>
 * <p>
 * If the executor decides to process the job asynchronously, the processing must finish
 * within the current lifetime of the job executor. If the executor (or the instance
 * of the executor) dies, the job processing will mark this processing as failed and
 * reschedule.
 *
 * @since 1.1 of org.apache.sling.event
 */
@ConsumerType
public interface JobExecutor {

    /**
     * Service registration property defining the jobs this executor is able to process.
     * The value is either a string or an array of strings.
     */
    String PROPERTY_TOPICS = "job.topics";

    /**
     * Execute the job.
     *
     * If the job has been processed successfully, a job result of "succeeded" should be returned. This result can
     * be generated by calling <code>JobExecutionContext.result().succeeded()</code>
     *
     * If the job has not been processed completely, but might be rescheduled "failed" should be returned.
     * This result can be generated by calling <code>JobExecutionContext.result().failed()</code>.
     *
     * If the job processing failed and should not be rescheduled, "cancelled" should be returned.
     * This result can be generated by calling <code>JobExecutionContext.result().cancelled()</code>.
     *
     * If the executor decides to process the job asynchronously it should return <code>null</code>
     * and notify the job manager by using the {@link JobExecutionContext#asyncProcessingFinished(JobExecutionResult)}
     * method of the processing result.
     *
     * If the processing fails with throwing an exception/throwable, the process will not be rescheduled
     * and treated like the method would have returned a "cancelled" result.
     *
     * Additional information can be added to the result by using the builder pattern available
     * from {@link JobExecutionContext#result()}.
     *
     * @param job The job
     * @param context The execution context.
     * @return The job execution result or <code>null</code> for asynchronous processing.
     */
    JobExecutionResult process(Job job, JobExecutionContext context);
}
