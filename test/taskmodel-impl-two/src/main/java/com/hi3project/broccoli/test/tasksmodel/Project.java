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

import java.util.ArrayList;
import java.util.Collection;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

/**
 *
 * 
 */
@Namespace("http://hi3project.com/broccoli/test/tasks#")
public class Project extends NamedThing {
    
    private Collection tasks = null;
    
    public Project() {
        tasks = new ArrayList();
    }
   
    public Project(String name) {
        this();
        setNameOF(name);
    }

    @RdfProperty("http://hi3project.com/broccoli/test/tasks#tasksFromProject")
    public Collection<Task> getTasks() {
        return tasks;
    }
    
   public Collection<Task> getTasksFromProject() {
        return getTasks();
    }

    public void setTasks(Collection tasks) {
        this.tasks = tasks;
    }
    
    public boolean containsTask(String taskName) {
        for (Task task : getTasks()) {
            if (task.getNameOF().equals(taskName)) {
                return true;
            }
        }
        return false;
    }
    
}
