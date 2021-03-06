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

package jlibs.xml.dom;

import jlibs.core.graph.Convertor;
import jlibs.core.lang.StringUtil;
import jlibs.xml.Namespaces;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

import javax.xml.namespace.NamespaceContext;

/**
 * @author Santhosh Kumar T
 */
public class DOMXPathNameConvertor implements Convertor<Node, String>{
    private NamespaceContext nsContext;

    public DOMXPathNameConvertor(NamespaceContext nsContext){
        this.nsContext = nsContext;
    }

    public DOMXPathNameConvertor(){}

    @Override
    public String convert(Node source){
        switch(source.getNodeType()){
            case Node.DOCUMENT_NODE:
                return "";
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE:
                return "text()";
            case Node.COMMENT_NODE:
                return "comment()";
            case Node.PROCESSING_INSTRUCTION_NODE:
                ProcessingInstruction pi = (ProcessingInstruction)source;
                return "processing-instruction('"+pi.getTarget() +"')";
            case Node.ELEMENT_NODE:
                if(nsContext!=null){
                    String prefix = nsContext.getPrefix(source.getNamespaceURI());
                    String name = source.getLocalName();
                    return StringUtil.isEmpty(prefix) ? name : prefix+':'+name;
                }else
                    return source.getNodeName();
            case Node.ATTRIBUTE_NODE:
                if(Namespaces.URI_XMLNS.equals(source.getNamespaceURI()))
                    return "namespace::"+source.getLocalName();
                
                if(nsContext!=null){
                    String prefix = nsContext.getPrefix(source.getNamespaceURI());
                    String name = source.getLocalName();
                    return '@'+ (StringUtil.isEmpty(prefix) ? name : prefix+':'+name);
                }else
                    return '@'+source.getNodeName();
            case 13: /*Node.NAMESPACE_NODE*/
                return "namespace::"+source.getLocalName();
            default:
                return null;
        }
    }
}
