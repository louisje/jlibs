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

package jlibs.xml.sax.sniff.model.computed;

import jlibs.xml.sax.sniff.Context;
import jlibs.xml.sax.sniff.events.Event;
import jlibs.xml.sax.sniff.model.Node;
import jlibs.xml.sax.sniff.model.ResultType;
import jlibs.xml.sax.sniff.model.Results;
import jlibs.xml.sax.sniff.model.UserResults;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Santhosh Kumar T
 */
public abstract class ComputedResults extends Node{
    @Override
    public boolean equivalent(Node node){
        return false;
    }
    
    public List<UserResults> members = new ArrayList<UserResults>();

    public Iterable<UserResults> members(){
        return members;
    }

    public void addMember(UserResults member, ResultType expected){
        if(member.resultType()!=expected)
            throw new IllegalArgumentException(expected.toString());

        root = ((Node)member).root;
        hits.totalHits = member.hits.totalHits;
        members.add(member);
        member.observers.add(this);
    }

    public abstract void memberHit(UserResults member, Context context, Event event);

    @Override
    public void notifyObservers(Context context, Event event){
        super.notifyObservers(context, event);
        if(userGiven)
            hits.hit();
    }

    public String getName(){
        return getClass().getSimpleName();
    }

    @NotNull
    protected abstract Results createResultCache();

    private Results resultCache;
    @SuppressWarnings({"unchecked"})
    public <T extends Results> T getResultCache(UserResults member, Context context){
        if(resultCache!=null)
            return (T)resultCache;

        ComputedResults node = this;
        while(node.observers.size()>0){
            node = node.observers.get(0);
            if(node instanceof FilteredNodeSet){
                FilteredNodeSet filteredNodeSet = (FilteredNodeSet)node;
                if(filteredNodeSet.contextSensitive){
                    return (T)filteredNodeSet.getResultCache(member, context).getResultCache(this);
                }
            }
        }

        resultCache = createResultCache();
        return (T)resultCache;
    }
    
    @SuppressWarnings({"unchecked"})
    public <T extends Results> T getResultCache(){
        return (T)resultCache;
    }

    protected void clearResults(Context context){
        if(resultCache!=null)
            resultCache=null;
        
        for(UserResults observer: members()){
            if(observer instanceof ComputedResults)
                ((ComputedResults)observer).clearResults(context);
        }
        for(ComputedResults observer: observers())
            observer.clearResults(this, context);
    }

    public void clearResults(UserResults member, Context context){}

    @Override
    public void endingContext(Context context){}

    @Override
    public String toString(){
        StringBuilder buff = new StringBuilder();
        if(userGiven)
            buff.append("UserGiven");
        for(UserResults member: members){
            if(buff.length()>0)
                buff.append(", ");
            buff.append('(').append(member).append(')');
        }
        return getName()+'{'+buff+'}';
    }
}
