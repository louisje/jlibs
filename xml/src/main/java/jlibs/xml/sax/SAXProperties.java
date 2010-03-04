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

package jlibs.xml.sax;

/**
 * This interface contains constants for various SAX Properties.
 * 
 * @author Santhosh Kumar T
 */
public interface SAXProperties{
    /**
     * Set the handler for lexical parsing events: comments, CDATA delimiters,
     * selected general entity inclusions, and the start and end of the DTD
     * (and declaration of document element name).
     * <p>
     * <b>Type:</b> {@link org.xml.sax.ext.LexicalHandler}<br>
     * <b>Access:</b> read-write
     */
    String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler"; //NOI18N

    /**
     * Set the handler for DTD declarations
     * <p>
     * <b>Type:</b> {@link org.xml.sax.ext.DeclHandler}<br>
     * <b>Access:</b> read-write
     */
    String DECL_HANDLER = "http://xml.org/sax/properties/declaration-handler"; //NOI18N

    /**
     * A literal string describing the actual XML version of the document, such as "1.0" or "1.1"
     * <p>
     * <b>Type:</b> {@link java.lang.String}<br>
     * <b>Access:</b> read-only<br>
     * <b>Note:</b> This property may only be examined during a parse after the startDocument callback has been completed.
     */
    String XML_VERSION = "http://xml.org/sax/properties/document-xml-version"; //NOI18N

    /**
     * Get the string of characters associated with the current event. <br>If the parser
     * recognizes and supports this property but is not currently parsing text,
     * it should return null
     * <p>
     * <b>Type:</b> {@link java.lang.String}<br>
     * <b>Access:</b> read-only
     */
    String XML_STRING = "http://xml.org/sax/properties/xml-string"; //NOI18N

    /**
     * The DOM node currently being visited, if SAX is being used as a DOM iterator.<br>
     * If the parser recognizes and supports this property but is not currently
     * visiting a DOM node, it should return null.
     * <p>
     * <b>Type:</b> {@link org.w3c.dom.Node}<br>
     * <b>Access:</b> (parsing) read-only; (not parsing) read-write;
     */
    String DOM_NODE = "http://xml.org/sax/properties/dom-node"; //NOI18N

    /*-------------------------------------------------[ Non-Standard ]---------------------------------------------------*/

    /**
     * Shortcut for SAX-ext. lexical handler alternate property.
     * Although this property URI is not the one defined by the SAX
     * "standard", some parsers use it instead of the official one.
     */
    String LEXICAL_HANDLER_ALT = "http://xml.org/sax/handlers/LexicalHandler"; //NOI18N

    /** Shortcut for SAX-ext. declaration handler alternate property */
    String DECL_HANDLER_ALT = "http://xml.org/sax/handlers/DeclHandler"; //NOI18N
}