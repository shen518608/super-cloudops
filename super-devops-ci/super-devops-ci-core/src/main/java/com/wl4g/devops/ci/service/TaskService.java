/*
 * Copyright 2017 ~ 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.ci.service;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.scm.AppInstance;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 11:43:00
 */
@Component
public interface TaskService {

    List<TaskHistory> list(String groupName, String projectName, String branchName);

    List<TaskHistoryDetail> getDetailByTaskId(Integer id);

    TaskHistory getTaskById(Integer id);

    TaskHistory createTask(Project project, List<AppInstance> instances, int type, int status, String branchName, String sha,
                           Integer parentId, String command, Integer tarType);

    void updateTaskStatus(int taskId, int status);

    void updateTaskStatusAndResult(int taskId, int status, String result);

    void updateTaskDetailStatusAndResult(int taskDetailId, int status, String result);

    void updateTaskStatusAndResultAndSha(int taskId, int status, String result, String sha, String md5);

}