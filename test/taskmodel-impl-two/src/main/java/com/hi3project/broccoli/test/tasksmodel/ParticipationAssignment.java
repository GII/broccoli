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

import thewebsemantic.Namespace;

/**
 *
 * 
 */
@Namespace("http://hi3project.com/broccoli/test/tasks#")
public class ParticipationAssignment {
    
    public ParticipationAssignment() {}
    
    public Task participationFromTask;
    public Role participationFromRole;
    public Participant participationFromParticipant;    

    public Participant getParticipationFromParticipant() {
        return participationFromParticipant;
    }

    public void setParticipationFromParticipant(Participant participationFromParticipant) {
        this.participationFromParticipant = participationFromParticipant;
    }

    public Role getParticipationFromRole() {
        return participationFromRole;
    }

    public void setParticipationFromRole(Role participationFromRole) {
        this.participationFromRole = participationFromRole;
    }

    public Task getParticipationFromTask() {
        return participationFromTask;
    }

    public void setParticipationFromTask(Task participationFromTask) {
        this.participationFromTask = participationFromTask;
    }
    
    
    
}
