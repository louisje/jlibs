/**
 * JLibs: Common Utilities for Java
 * Copyright (C) 2009  Santhosh Kumar T <santhosh.tekuri@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package jlibs.xml.sax.dog.expr.nodset;

import jlibs.xml.sax.dog.DataType;
import jlibs.xml.sax.dog.sniff.Event;

/**
 * @author Santhosh Kumar T
 */
public final class ExactPosition extends Positional{
    public final int pos;

    public ExactPosition(int pos){
        super(DataType.BOOLEAN, true);
        this.pos = pos;
    }

    @Override
    public Object getResult(Event event){
        if(predicate==null)
            return event.positionTrackerStack.peekFirst().position==pos;
        else
            return super.getResult(event);
    }

    @Override
    protected Object translate(Double result){
        return result.intValue()==pos;
    }

    @Override
    public String toString(){
        return String.format("exact-position(%d)", pos);
    }
}
