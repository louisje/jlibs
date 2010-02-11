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
import jlibs.xml.sax.dog.expr.Evaluation;
import jlibs.xml.sax.dog.expr.EvaluationListener;
import jlibs.xml.sax.dog.expr.Expression;
import jlibs.xml.sax.dog.path.Axis;
import jlibs.xml.sax.dog.path.AxisListener;
import jlibs.xml.sax.dog.path.EventID;
import jlibs.xml.sax.dog.path.Step;
import jlibs.xml.sax.dog.path.tests.Text;
import jlibs.xml.sax.dog.sniff.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Santhosh Kumar T
 */
public final class StringEvaluation extends AxisListener<Strings>{
    private static final Step DESCENDANT_TEXT_STEP = new Step(Axis.DESCENDANT, Text.INSTANCE);

    private StringBuilder buff = new StringBuilder(50);
    private Event event;
    private EventID eventID;

    protected StringEvaluation(Strings expression, Event event){
        super(expression, event.order());
        this.event = event;
        this.eventID = event.getID();
    }

    @Override
    public void start(){
        eventID.addListener(event, DESCENDANT_TEXT_STEP, this);
    }

    @Override
    public Object getResult(){
        return result;
    }

    @Override
    public void finished(Evaluation evaluation){}

    @Override
    public void onHit(EventID eventID){
        StringBuilder str = event.buff;
        buff.append(str);
        if(numberListeners.size()>0){
            for(int i=str.length()-1; i>=0; --i){
                char ch = str.charAt(i);
                if(!Character.isDigit(ch) && ch!='.' && ch!='+' && ch!='-'){
                    fireFinished(numberListeners, Double.NaN);
                    if(stringListeners.size()==0)
                        manuallyExpired = true; // i.e dispose()
                    return;
                }
            }
        }else if(stringListeners.size()==0) // we can't dispose in removeListener because this is being reused
            manuallyExpired = true; // i.e dispose()
    }

    @Override
    public void expired(){
        fireFinished();
    }

    public ArrayList<EvaluationListener> numberListeners = new ArrayList<EvaluationListener>();
    public ArrayList<EvaluationListener> stringListeners = new ArrayList<EvaluationListener>();

    @Override
    public void addListener(EvaluationListener listener){
        addListener(expression, listener);
    }

    public void addListener(Expression expression, EvaluationListener listener){
        if(expression.resultType==DataType.NUMBER || expression.resultType==DataType.NUMBERS)
            numberListeners.add(listener);
        else
            stringListeners.add(listener);
    }

    @Override
    public void removeListener(EvaluationListener listener){
        removeListener(expression, listener);
    }

    public void removeListener(Expression expression, EvaluationListener listener){
        if(firing)
            listener.disposed = true;
        else{
            if(expression.resultType==DataType.NUMBER || expression.resultType==DataType.NUMBERS)
                numberListeners.remove(listener);
            else
                stringListeners.remove(listener);
        }
    }

    private boolean firing;
    private Object result;
    private void fireFinished(List<EvaluationListener> listeners, Object result){
        this.result = result;
        firing = true;
        for(EvaluationListener listener: listeners){
            if(!listener.disposed)
                listener.finished(this);
        }
        firing = false;
        listeners.clear();
    }

    @Override
    public void fireFinished(){
        if(stringListeners.size()>0)
            fireFinished(stringListeners, buff.toString());
        if(numberListeners.size()>0){
            double d;
            try{
                d = Double.parseDouble(buff.toString());
            }catch(NumberFormatException ex){
                d = Double.NaN;
            }
            fireFinished(numberListeners, d);
        }
    }

    protected void dispose(){
        manuallyExpired = true;
    }
}