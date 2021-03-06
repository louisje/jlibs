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

package jlibs.nblr.matchers;

import jlibs.core.lang.StringUtil;

import java.util.List;

/**
 * @author Santhosh Kumar T
 */
public abstract class Matcher{
    public String name;
    public String javaCode;
    public int usageCount;

    public abstract String toString();
    protected final String _toString(){
        if(name==null)
            return toString();
        else
            return "[<"+name+">]";
    }

    public static String toJava(int codePoint){
        if(codePoint>0x0020 && codePoint<0x007f) // visible character in ascii)
            return '\''+StringUtil.toLiteral((char)codePoint, false)+'\'';
        else
            return "0x"+Integer.toHexString(codePoint);
    }
    
    private static String SPECIALS = "\\#.[]-^&";
    protected static String encode(int... chars){
        if(chars==null || chars.length==0)
            return ".";
        StringBuilder buff = new StringBuilder();
        for(int ch: chars){
            if(SPECIALS.indexOf(ch)!=-1)
                buff.append('\\').append((char)ch);
            else{
                if(ch=='"')
                    buff.append((char)ch);
                else{
                    if(ch>0x0020 && ch<0x007f) // visible character in ascii)
                        buff.append(StringUtil.toLiteral(""+(char)ch, false));
                    else
                        buff.append("#x").append(Integer.toHexString(ch)).append(';');
                }
            }
        }
        return buff.toString();
    }

    public boolean hasCustomJavaCode(){
        return javaCode!=null;
    }
    
    public boolean canInline(){
        return javaCode!=null || usageCount<=1;
    }

    protected abstract String __javaCode(String variable);
    public final String javaCode(String variable){
        if(javaCode==null)
            return __javaCode(variable);
        else
            return javaCode.replace("$codePoint", variable);
    }
    public final String _javaCode(String variable){
        if(name==null || canInline())
            return javaCode(variable);
        else
            return name+'('+variable+')';
    }

    public boolean checkFor(int min){
        Not.minValue = min;
        try{
            return hasCustomJavaCode() || clashesWith(new Any(-1));
        }finally{
            Not.minValue = Character.MIN_VALUE;
        }
    }
    
    /*-------------------------------------------------[ Ranges ]---------------------------------------------------*/

    public abstract List<jlibs.core.util.Range> ranges();

    public boolean clashesWith(Matcher that){
        return !jlibs.core.util.Range.intersection(this.ranges(), that.ranges()).isEmpty();
    }

    public boolean same(Matcher that){
        return jlibs.core.util.Range.same(this.ranges(), that.ranges());
    }

    /*-------------------------------------------------[ Factory ]---------------------------------------------------*/

    public static Matcher any(String chars){
        return new Any(chars);
    }

    public static Matcher any(){
        return new Any();
    }

    public static Matcher ch(char ch){
        return new Any(ch);
    }

    public static Matcher range(String range){
        return new Range(range);
    }

    public static Matcher not(Matcher ch){
        return new Not(ch);
    }

    public static Matcher and(Matcher... operands){
        return new And(operands);
    }

    public static Matcher or(Matcher... operands){
        return new Or(operands);
    }

    public static Matcher minus(Matcher lhs, Matcher rhs){
        return new And(lhs, not(rhs));
    }

    public static void main(String[] args){
        
    }
}