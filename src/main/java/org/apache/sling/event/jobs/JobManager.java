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
 * @deprecated JobManager is deprecated, specifically its job queries, use JobManager2 instead - for more details see https://sling.apache.org/documentation/bundles/apache-sling-eventing-and-job-handling.html
 * @since 3.0 of org.apache.sling.event
 */
@ProviderType
@Deprecated
public interface JobManager extends JobManager2 {

    /**
     * The requested job types for the query.
     * This can either be all (unfinished) jobs, all activated (started) or all queued jobs.
     * @deprecated job queries are deprecated - for more details see https://sling.apache.org/documentation/bundles/apache-sling-eventing-and-job-handling.html
     */
    @Deprecated
    enum QueryType {
        ALL,      // all means all active and all queued
        ACTIVE,
        QUEUED,
        HISTORY,    // returns the complete history of cancelled and succeeded jobs (if available)
        CANCELLED,  // history of cancelled jobs (STOPPED, GIVEN_UP, ERROR, DROPPED)
        SUCCEEDED,  // history of succeeded jobs
        STOPPED,    // history of stopped jobs
        GIVEN_UP,   // history of given up jobs
        ERROR,      // history of jobs signaled CANCELLED or throw an exception
        DROPPED     // history of dropped jobs
    }

    /**
     * Return a job based on the unique id.
     *
     * The returned job object is a snapshot of the job state taken at the time of the call. Updates
     * to the job state are not reflected and the client needs to get a new job object using the job id.
     *
     * @param jobId The unique identifier from {@link Job#getId()}
     * @return A job or <code>null</code>
     * @deprecated job queries are deprecated - for more details see https://sling.apache.org/documentation/bundles/apache-sling-eventing-and-job-handling.html
     * @since 1.2 of org.apache.sling.event
     */
    @Deprecated
    Job getJobById(String jobId);

    /**
     * Find a job - either queued or active.
     *
     * This method searches for a job with the given topic and filter properties. If more than one
     * job matches, the first one found is returned which could be any of the matching jobs.
     *
     * The returned job object is a snapshot of the job state taken at the time of the call. Updates
     * to the job state are not reflected and the client needs to get a new job object using the job id.
     *
     * @param topic Topic is required.
     * @param template The map acts like a template. The searched job
     *                    must match the template (AND query).
     * @return A job or <code>null</code>
     * @deprecated job queries are deprecated - for more details see https://sling.apache.org/documentation/bundles/apache-sling-eventing-and-job-handling.html
     * @since 1.2 of org.apache.sling.event
     */
    @Deprecated
    Job getJob(String topic, Map<String, Object> template);

    /**
     * Return all jobs of a given type.
     *
     * Based on the type parameter, either the history of jobs can be returned or unfinished jobs. The type
     * parameter can further specify which category of jobs should be returned: for the history either
     * succeeded jobs, cancelled jobs or both in combination can be returned. For unfinished jobs, either
     * queued jobs, started jobs or the combination can be returned.
     * If the history is returned, the result set is sorted in descending order, listening the newest entry
     * first. For unfinished jobs, the result set is sorted in ascending order.
     *
     * The returned job objects are a snapshot of the jobs state taken at the time of the call. Updates
     * to the job states are not reflected and the client needs to get new job objects.
     *
     * @param type Required parameter for the type. See above.
     * @param topic Topic can be used as a filter, if it is non-null, only jobs with this topic will be returned.
     * @param limit A positive number indicating the maximum number of jobs returned by the iterator. A value
     *              of zero or less indicates that all jobs should be returned.
     * @param templates A list of filter property maps. Each map acts like a template. The searched job
     *                    must match the template (AND query). By providing several maps, different filters
     *                    are possible (OR query).
     * @return A collection of jobs - the collection might be empty.
     * @deprecated job queries are deprecated - for more details see https://sling.apache.org/documentation/bundles/apache-sling-eventing-and-job-handling.html
     * @since 1.2 of org.apache.sling.event
     */
    @Deprecated
    Collection<Job> findJobs(QueryType type, String topic, long limit, Map<String, Object>... templates);

    /**
     * Retry a cancelled job.
     * If a job has failed permanently it can be requeued with this method. The job will be
     * removed from the history and put into the queue again. The new job will get a new job id.
     * For all other jobs calling this method has no effect and it simply returns <code>null</code>.
     * @param jobId The job id.
     * @deprecated job queries are deprecated - for more details see https://sling.apache.org/documentation/bundles/apache-sling-eventing-and-job-handling.html
     * @return If the job is requeued, the new job object otherwise <code>null</code>
     */
    @Deprecated
    Job retryJobById(String jobId);

}
