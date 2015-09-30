/*******************************************************************************
 *
 * Copyright (C) 2015 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright (C) 2015 Alejandro Paz <alejandropl@lagostelle.com>
 *
 * This file is part of Broccoli.
 *
 * Broccoli is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Broccoli is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Broccoli. If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.hi3project.broccoli.test.tasksmodel;

import java.util.Collection;

/**
 *
 * 
 */
public class ParticipantsOfTask {
    
    public static final String projectName = "Tortugas";
    
    
    public ParticipantsOfTask()
    {
        
    }
    
    public String retrieve(Object nameOfTask) {
        return "task1 and task2";
    }
    
    public Task getNewTask(String taskName) {
        return new Task(taskName);
    }
    
    public String getTaskName(Task task) {
        return task.getNameOF();
    }
    
    public int taskCount() {
        return 5;
    }
    
    public Participant getParticipant(Object something) {
        Participant gerv = new Participant();
        gerv.setNameOF(participantExample);
        return gerv;
    }
    
    public static final String participantExample = "Gervasio";
    
    
    public Participant getParticipantOf(ParticipationAssignment pa) {
        return pa.getParticipationFromParticipant();
    }
    
    public Collection<Task> getTasksOf(Project project) {
        return project.getTasks();        
    }
    
    public Project buildProjectFrom(Collection tasks) {
        Project project = new Project(projectName);
        for (Object task : tasks) {
            project.getTasks().add((Task)task);
        }
        return project;
    }
    
}
