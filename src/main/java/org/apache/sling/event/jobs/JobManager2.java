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
package org.apache.sling.event.jobs;

import java.util.Collection;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;


/**
 * The job manager is the heart of the job processing.
 * <p>
 * The job manager allows to create new jobs, search for
 * jobs and get statistics about the current state.
 * <p>
 * The terminology used in the job manager is slightly
 * different from common terminology:
 * Each job has a topic and a topic is associated with
 * a queue. Queues can be created through configuration
 * and each queue can process one or more topics.
 *
 * @since 1.1 of org.apache.sling.event.api
 */
@ProviderType
public interface JobManager2 {

    /**
     * Return statistics information about all queues.
     * @return The statistics.
     */
    Statistics getStatistics();

    /**
     * Return statistics information about job topics.
     * @return The statistics for all topics.
     */
    Iterable<TopicStatistics> getTopicStatistics();

    /**
     * Return a queue with a specific name (if running)
     * @param name The queue name
     * @return The queue or <code>null</code>
     */
    Queue getQueue(String name);

    /**
     * Return an iterator for all available queues.
     * @return An iterator for all queues.
     */
    Iterable<Queue> getQueues();

    /**
     * Add a new job
     *
     * If the topic is <code>null</code> or illegal, no job is created and <code>null</code> is returned.
     * If properties are provided, all of them must be serializable. If there are non serializable
     * objects in the properties, no job is created and <code>null</code> is returned.
     * A job topic is a hierarchical name separated by dashes, each part has to start with a letter,
     * allowed characters are letters, numbers and the underscore.
     *
     * The returned job object is a snapshot of the job state taken at the time of creation. Updates
     * to the job state are not reflected and the client needs to get a new job object using the job id.
     *
     * If the queue for processing this job is configured to drop the job, <code>null</code> is returned
     * as well.
     *
     * @param topic The required job topic.
     * @param properties Optional job properties. The properties must be serializable.
     * @return The new job - or <code>null</code> if the job could not be created.
     * @since 1.2 of org.apache.sling.event
     */
    Job addJob(String topic, Map<String, Object> properties);

    /**
     * Removes the job even if it is currently in processing.
     *
     * If the job exists and is not in processing, it gets removed from the processing queue.
     * If the job exists and is in processing, it is removed from the persistence layer,
     * however processing is not stopped.
     *
     * @param jobId The unique identifier from {@link Job#getId()}
     * @return <code>true</code> if the job could be removed or does not exist anymore.
     *         <code>false</code> otherwise.
     * @since 1.2 of org.apache.sling.event
     */
    boolean removeJobById(String jobId);

    /**
     * Stop a job.
     * When a job is stopped and the job consumer supports stopping the job processing, it is up
     * to the job consumer how the stopping is handled. The job can be marked as finished successful,
     * permanently failed or being retried.
     * @param jobId The job id
     * @since 1.3 of org.apache.sling.event
     */
    void stopJobById(String jobId);

    /**
     * Fluent API to create, start and schedule new jobs
     * @param topic Required topic
     * @return A job builder
     * @since 1.3 of org.apache.sling.event
     */
    JobBuilder createJob(final String topic);

    /**
     * Return all available job schedules.
     * @return A collection of scheduled job infos
     * @since 1.3 of org.apache.sling.event
     */
    Collection<ScheduledJobInfo> getScheduledJobs();

    /**
     * Return all matching available job schedules.
     * @param topic Topic can be used as a filter, if it is non-null, only jobs with this topic will be returned.
     * @param limit A positive number indicating the maximum number of jobs returned by the iterator. A value
     *              of zero or less indicates that all jobs should be returned.
     * @param templates A list of filter property maps. Each map acts like a template. The searched job
     *                    must match the template (AND query). By providing several maps, different filters
     *                    are possible (OR query).
     * @return All matching scheduled job infos.
     * @since 1.4 of org.apache.sling.event
     */
    Collection<ScheduledJobInfo> getScheduledJobs(String topic, long limit, Map<String, Object>... templates);
}
