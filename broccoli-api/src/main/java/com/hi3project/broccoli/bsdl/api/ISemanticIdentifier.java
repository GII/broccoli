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

package com.hi3project.broccoli.bsdl.api;

import java.net.URI;

/**
 * <p>
 * An identifier for semantic axioms, in the form of an URI that has at least
 * two different parts: authority name, and identifier name
 * </p>
 * <p>
 * The identifier does not hold info about a physical location. Protocol does
 * not matter, we use http:// just because its common use.
 * </p>
 * <p>
 * Any identifier name must be unique for itself within the same authority name.
 * An example: http://hi3project.com/broccoli/bsdm#serviceDescription
 * </p>
 * <ul>
 * <li>http://gii.udc.es/semanticServices would be the authority name</li>
 * <li>bsdm/serviceDescription would be the identifier name, and it should be
 * unique for each identifier that holds the previous authority name</li>
 * </ul>
 *
 * 
 */
public interface ISemanticIdentifier
{

    public URI getURI();

    public String getAuthorityName();

    public String getName();

    public String getLastName();

}
