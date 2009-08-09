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

package jlibs.xml.sax.sniff.events;

import jlibs.xml.sax.sniff.engine.data.LocationStack;

/**
 * @author Santhosh Kumar T
 */
public class Namespace extends Event{
    public Namespace(DocumentOrder documentOrder, LocationStack locationStack){
        super(documentOrder, locationStack);
    }

    @Override
    public int type(){
        return NAMESPACE;
    }

    @Override
    public String location(){
        return locationStack.namespace(prefix);
    }

    @Override
    protected String value(){
        return uri;
    }

    @Override
    public String localName(){
        return prefix;
    }

    @Override
    public String namespaceURI(){
        return "";
    }

    @Override
    public String qualifiedName(){
        return prefix;
    }

    public String prefix;
    public String uri;

    public void setData(String prefix, String uri){
        this.prefix = prefix;
        this.uri = uri;
        hit();
    }

    @Override
    public String toString(){
        if(prefix.length()==0)
            return String.format("xmlns='%s'", uri);
        else
            return String.format("xmlns:%s='%s'", prefix, uri);
    }
}