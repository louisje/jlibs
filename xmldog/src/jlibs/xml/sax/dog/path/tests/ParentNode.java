/**
 * JLibs: Common Utilities for Java
 * Copyright (C) 2009  Santhosh Kumar T
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package jlibs.xml.sax.dog.path.tests;

import jlibs.xml.sax.dog.NodeType;
import jlibs.xml.sax.dog.path.Constraint;
import jlibs.xml.sax.dog.sniff.Event;

/**
 * @author Santhosh Kumar T
 */
public final class ParentNode extends Constraint{
    public static final ParentNode INSTANCE = new ParentNode();

    private ParentNode(){
        super(ID_PARENTNODE);
    }

    @Override
    public boolean matches(Event event){
        switch(event.type()){
            case NodeType.DOCUMENT:
            case NodeType.ELEMENT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString(){
        return "pnode()";
    }
}