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

package com.hi3project.broccoli.test.tareasmodel;

import java.util.ArrayList;
import java.util.Collection;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

/**
 *
 * 
 */
@Namespace("http://hi3project.com/broccoli/test/tasks#")
@RdfType("Project")
public class Proyecto extends ObjetoConNombre {
    
    private Collection<Tarea> tareas = new ArrayList<Tarea>();
    
    public Proyecto() {}
    
    public Proyecto(String nombre) {
        this.setNombre(nombre);
    }

    @RdfProperty("http://hi3project.com/broccoli/test/tasks#tasksFromProject")
    public Collection<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(Collection<Tarea> tareas) {
        this.tareas = tareas;
    }
    
    public Proyecto anadirTarea(Tarea tarea) {
        getTareas().add(tarea);
        return this;
    }
    
    public boolean contieneLaTarea(String taskName) {
        for (Tarea tarea : getTareas()) {
            if (tarea.getNombre().equals(taskName)) {
                return true;
            }
        }
        return false;
    }
}
