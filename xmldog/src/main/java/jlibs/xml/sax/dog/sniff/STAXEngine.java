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

package jlibs.xml.sax.dog.sniff;

import jlibs.xml.Namespaces;
import jlibs.xml.sax.helpers.MyNamespaceSupport;
import org.xml.sax.InputSource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathException;

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * @author Santhosh Kumar T
 */
public final class STAXEngine{
    final Event event;
    final boolean langInterested;

    public STAXEngine(Event event, boolean langInterested){
        this.event = event;
        this.langInterested = langInterested;
    }

    private static final ThreadLocal<XMLInputFactory> factoryLocal = new ThreadLocal<XMLInputFactory>();

    private static XMLInputFactory getFactory(){
        XMLInputFactory factory = factoryLocal.get();
        if(factory==null){
            factory = XMLInputFactory.newInstance();
            factoryLocal.set(factory);
        }
        return factory;
    }

    public void start(InputSource is) throws XPathException{
        XMLStreamReader reader = null;
        try{
            StreamSource source = new StreamSource();
            source.setSystemId(is.getSystemId());
            source.setInputStream(is.getByteStream());
            source.setReader(is.getCharacterStream());
            reader = getFactory().createXMLStreamReader(source);
            int eventType = reader.getEventType();
            MyNamespaceSupport nsSupport = new MyNamespaceSupport();
            Event event = this.event;
            boolean langInterested = this.langInterested;
            while(true){
                switch(eventType){
                    case START_DOCUMENT:
                        event.onStartDocument();
                        break;
                    case END_DOCUMENT:
                        event.onEndDocument();
                        return;

                    case START_ELEMENT:
                        event.onText();

                        String localName = reader.getLocalName();
                        String prefix = reader.getPrefix();
                        String qname = prefix==null || prefix.length()==0 ? localName : prefix+':'+localName;
                        String lang = langInterested ? reader.getAttributeValue(Namespaces.URI_XML, "lang") : null;
                        String uri = reader.getNamespaceURI();
                        if(uri==null)
                            uri = "";
                        event.onStartElement(uri, localName, qname, lang);

                        nsSupport.pushContext();
                        for(int i=0, len=reader.getNamespaceCount(); i<len; i++){
                            prefix = reader.getNamespacePrefix(i);
                            uri = reader.getNamespaceURI(i);
                            if(uri==null)
                                uri = "";
                            nsSupport.declarePrefix(prefix==null?"":prefix, uri);
                        }
                        event.onNamespaces(nsSupport);
                        event.onAttributes(reader);
                        break;
                    case END_ELEMENT:
                        event.onText();
                        event.onEndElement();
                        nsSupport.popContext();
                        break;

                    case CHARACTERS:
                    case CDATA:
                        event.appendText(reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
                        break;

                    case COMMENT:
                        event.onText();
                        event.onComment(reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
                        break;

                    case PROCESSING_INSTRUCTION:
                        event.onText();
                        event.onPI(reader.getPITarget(), reader.getPIData());
                }
                eventType = reader.next();
            }
        }catch(Exception ex){
            if(ex!=Event.STOP_PARSING)
                throw new XPathException(ex);
        }finally{
            if(reader!=null){
                try{
                    reader.close();
                }catch(XMLStreamException ex){
                    //noinspection ThrowFromFinallyBlock
                    throw new XPathException(ex);
                }
            }
        }
    }
}
