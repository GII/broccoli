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

package com.hi3project.broccoli.bsdl.impl.parsing.xml.dom;

import org.w3c.dom.Node;

/**
 * <p>
 * <b>Description:</b></p>
 *  A simple wrapper for DOM Node objects that knows its type (Element, Attr...)
 *
 *
 * <p><b>Creation date:</b> 
 * 24-11-2014 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 24-11-2014 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class DOMNodeContainer 
{
    
    private int type;
    private Node node;
    
    public DOMNodeContainer(Node node, int type)
    {
        this.node = node;
        this.type = type;
    }
    
    public DOMNodeContainer(Node node)
    {
        this(node, Node.ELEMENT_NODE);
    }
    
    public int getType()
    {
        return this.type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public Node getNode()
    {
        return this.node;
    }

}
