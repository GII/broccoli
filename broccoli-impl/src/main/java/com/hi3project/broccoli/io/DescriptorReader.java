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

package com.hi3project.broccoli.io;

import com.hi3project.broccoli.bsdl.api.ISemanticLocator;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;

/**
 *
 * <p><b>Creation date:</b> 
 * 03-02-2015 </p>
 *
 * <p><b>Changelog:</b></p>
 * <ul>
 * <li> 1 , 03-02-2015 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class DescriptorReader 
{

    private ISemanticLocator locator;

    
    public DescriptorReader(ISemanticLocator locator)
    {
        this.locator = locator;
    }
    
    public String readAsString() throws DescriptorIOException
    {
        byte[] encoded = this.readAsBytes();
        try {
            return new String(encoded, DescriptorWriter.CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new DescriptorIOException("Cannot read from: " + locator.toString(), e);
        }
    }
    
    public byte[] readAsBytes() throws DescriptorIOException
    {
        try
        {
            return FileUtils.readFileToByteArray(new File(locator.getURI().getPath()));
        } catch (IOException ex)
        {
            throw new DescriptorIOException("Cannot read descriptor: " + locator.getLastName(), ex);
        }
    }
    
}
