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

package jlibs.nblr.editor.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Santhosh Kumar T
 */
public class ChoiceAction extends AbstractAction{
    private Action choices[];
    
    public ChoiceAction(String name, Action... choices){
        super(name);
        this.choices = choices;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae){
        for(Action choice: choices){
            if(choice.isEnabled()){
                choice.actionPerformed(ae);
                return;
            }
        }
    }

    @Override
    public boolean isEnabled(){
        for(Action choice: choices){
            if(choice.isEnabled())
                return true;
        }
        return false;
    }
}
