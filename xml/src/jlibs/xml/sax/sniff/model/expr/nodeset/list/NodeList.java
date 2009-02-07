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

package jlibs.xml.sax.sniff.model.expr.nodeset.list;

import jlibs.xml.sax.sniff.engine.context.Context;
import jlibs.xml.sax.sniff.events.Event;
import jlibs.xml.sax.sniff.events.PI;
import jlibs.xml.sax.sniff.model.ContextListener;
import jlibs.xml.sax.sniff.model.Datatype;
import jlibs.xml.sax.sniff.model.Node;
import jlibs.xml.sax.sniff.model.Notifier;
import jlibs.xml.sax.sniff.model.axis.Descendant;
import jlibs.xml.sax.sniff.model.expr.Expression;
import jlibs.xml.sax.sniff.model.expr.nodeset.ValidatedExpression;
import org.jaxen.saxpath.Axis;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Santhosh Kumar T
 */
public abstract class NodeList extends ValidatedExpression{
    public NodeList(Datatype returnType, Node contextNode, Notifier member, Expression predicate){
        super(returnType, contextNode, member, predicate);
    }

    private boolean textOnly;
 
    @Override
    public void addMember(Notifier member){
        if(member instanceof Node){
            Node node = (Node)member;
            if(node.canBeContext()){
                node.addContextListener(new ContextListener(){
                    @Override
                    public void contextStarted(Context context, Event event){
                        StringsEvaluation evaluation = (StringsEvaluation)evaluationStack.peek();
                        if(!evaluation.finished && !evaluation.resultPrepared)
                            evaluation.contextStarted(context);
                    }

                    @Override
                    public void contextEnded(Context context){
                        StringsEvaluation evaluation = (StringsEvaluation)evaluationStack.peek();
                        if(!evaluation.finished && !evaluation.resultPrepared)
                            evaluation.contextEnded(context);
                    }

                    @Override
                    public int priority(){
                        return evalDepth;
                    }
                });
                node = node.addChild(new Descendant(Axis.DESCENDANT));
                textOnly = true;

            }
            member = node;
        }
        super.addMember(member);
    }

    protected abstract class StringsEvaluation extends DelayedEvaluation{
        protected Map<Object, StringBuilder> map = new HashMap<Object, StringBuilder>();
        
        @Override
        public void finish(){
            for(StringBuilder buff: map.values()){
                if(buff.length()>0)
                    consume(buff.toString());
            }
            map = null;
            super.finish();
        }

        private void consume(Event event){
            String str = null;
            if(textOnly){
                if(event.type()==Event.TEXT){
                    StringBuilder buff = map.get(context.identity());
                    if(buff==null)
                        buff = map.get(context.parent.identity());
                    if(buff!=null)
                        buff.append(event.getResult());
                    else
                        System.out.println("");
                }
            }else{
                switch(event.type()){
                    case Event.TEXT:
                    case Event.COMMENT:
                    case Event.ATTRIBUTE:
                        str = event.getResult();
                        break;
                    case Event.PI:
                        str = ((PI)event).data;
                        break;
                }
                consume(str);
            }
        }

        protected abstract void consume(Object result);
        protected abstract void consume(String str);

        @Override
        @SuppressWarnings({"unchecked"})
        protected void consumeMemberResult(Object result){
            if(result instanceof Event)
                consume((Event)result);
            else
                consume(result);
        }

        public void contextStarted(Context context){
            map.put(context.identity(), new StringBuilder());
        }
        
        public void contextEnded(Context context){
            StringBuilder buff = map.remove(context.identity());
            if(buff!=null)
                consume(buff.toString());
            else
                System.out.println("");
        }
    }
}
