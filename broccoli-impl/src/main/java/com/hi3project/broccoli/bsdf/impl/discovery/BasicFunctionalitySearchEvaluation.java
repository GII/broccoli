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

package com.hi3project.broccoli.bsdf.impl.discovery;

import com.hi3project.broccoli.bsdf.api.discovery.IFunctionalitySearchEvaluation;

/**
 *
 * <p>
 * <b>Creation date:</b>
 * 14-05-2015 </p>
 *
 * <b>Changelog:</b>
 * <ul>
 * <li> 1 , 14-05-2015 - Initial release</li>
 * </ul>
 *
 * 
 * @version 1
 */
public class BasicFunctionalitySearchEvaluation implements IFunctionalitySearchEvaluation
{

    private int value = 0;

    private boolean requiredPropertiesPresent = false;

    public BasicFunctionalitySearchEvaluation(int value, boolean requiredPropertiesPresent)
    {
        this.value = value;
        this.requiredPropertiesPresent = requiredPropertiesPresent;
    }

    public int getValue()
    {
        return this.value;
    }

    public boolean areRequiredPropertiesPresent()
    {
        return this.requiredPropertiesPresent;
    }

    @Override
    public boolean isBetterThan(IFunctionalitySearchEvaluation evaluation)
    {

        if (evaluation instanceof BasicFunctionalitySearchEvaluation)
        {

            BasicFunctionalitySearchEvaluation bevaluation = (BasicFunctionalitySearchEvaluation) evaluation;
            if (this.areRequiredPropertiesPresent() == bevaluation.areRequiredPropertiesPresent())
            {
                return this.getValue() > bevaluation.getValue();
            } else
            {
                return this.areRequiredPropertiesPresent();
            }

        }

        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean isWorseThan(IFunctionalitySearchEvaluation evaluation)
    {
        if (evaluation instanceof BasicFunctionalitySearchEvaluation)
        {

            BasicFunctionalitySearchEvaluation bevaluation = (BasicFunctionalitySearchEvaluation) evaluation;
            if (this.areRequiredPropertiesPresent() == bevaluation.areRequiredPropertiesPresent())
            {
                return this.getValue() < bevaluation.getValue();
            } else
            {
                return !this.areRequiredPropertiesPresent();
            }

        }

        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean isEquivalentTo(IFunctionalitySearchEvaluation evaluation)
    {

        if (evaluation instanceof BasicFunctionalitySearchEvaluation)
        {

            BasicFunctionalitySearchEvaluation bevaluation = (BasicFunctionalitySearchEvaluation) evaluation;
            return (this.areRequiredPropertiesPresent() == bevaluation.areRequiredPropertiesPresent()
                    && this.getValue() == bevaluation.getValue());

        }

        throw new UnsupportedOperationException("Not supported");
    }

}
