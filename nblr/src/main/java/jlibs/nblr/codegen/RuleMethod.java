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

package jlibs.nblr.codegen;

import jlibs.core.annotation.processing.Printer;
import jlibs.nblr.codegen.java.SyntaxClass;
import jlibs.nblr.rules.Node;
import jlibs.nblr.rules.Rule;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static jlibs.core.annotation.processing.Printer.MINUS;
import static jlibs.core.annotation.processing.Printer.PLUS;

/**
 * @author Santhosh Kumar T
 */
public class RuleMethod{
    public final SyntaxClass syntaxClass;
    public final Rule rule;
    public final List<State> states = new ArrayList<State>();

    public RuleMethod(SyntaxClass syntaxClass, Rule rule){
        this.syntaxClass = syntaxClass;
        this.rule = rule;

        ArrayList<Node> statesVisited = new ArrayList<Node>();
        LinkedHashSet<Node> statesPending = new LinkedHashSet<Node>();
        statesPending.add(rule.node);

        while(!statesPending.isEmpty()){
            List<Node> list = new ArrayList<Node>(statesPending);
            Node pendingNode = list.get(list.size()-1);
            statesPending.remove(pendingNode);
            statesVisited.add(pendingNode);
            State state = new State(this, pendingNode);
            states.add(state);
            state.computeNextStates(statesVisited, statesPending);
            if(statesPending.isEmpty()){
                for(Node node: rule.nodes()){
                    if(node.name!=null){
                        if(!statesVisited.contains(node))
                            statesPending.add(node);
                    }
                }
            }
        }
    }

    public int maxLookAhead(){
        int maxLookAhead = 0;
        for(State state: states)
            maxLookAhead = Math.max(maxLookAhead, state.maxLookAhead());
        return maxLookAhead;
    }

    public boolean readCodePoint(){
        for(State state: states){
            if(state.readCodePoint())
                return true;
        }
        return false;
    }

    public boolean requiresWhile(){
        for(int i=0; i<states.size(); i++){
            State state = states.get(i);
            State nextState = i+1<states.size() ? states.get(i+1) : null;
            if(state.requiresContinue(nextState))
                return true;
        }
        return false;
    }

    public void generate(Printer printer){
        printer.println("public static final int RULE_"+rule.name.toUpperCase()+" = "+rule.id+';');
        
        printer.printlns(
            "private boolean "+rule.name+"(int state) throws Exception{",
                PLUS
        );

        if(readCodePoint())
            printer.println("int ch;");
        
        boolean requiresWhile = requiresWhile();
        if(requiresWhile){
            printer.printlns(
                "while(true){",
                    PLUS
            );
        }

        printer.printlns(
            "switch(state){",
                PLUS
        );
        for(int i=0; i<states.size(); i++){
            State state = states.get(i);
            State nextState = i+1<states.size() ? states.get(i+1) : null;
            state.generate(printer, nextState);
        }

        printer.printlns(
                "default:",
                    PLUS,
                    "throw new Error(\"impossible state: \"+state);",
                    MINUS,
                MINUS,
            "}"
        );

        if(requiresWhile){
            printer.printlns(
                    MINUS,
                "}"
            );
        }

        printer.printlns(
                MINUS,
            "}"
        );
    }

    @Override
    public String toString(){
        StringWriter writer = new StringWriter();
        Printer printer = new Printer(new PrintWriter(writer, true));
        generate(printer);
        printer.close();
        return writer.toString();
    }
}
