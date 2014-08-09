/*
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

package jlibs.nio.http.msg.spec.values;

import jlibs.nio.http.msg.spec.Parser;

import static java.util.Objects.requireNonNull;

/**
 * @author Santhosh Kumar Tekuri
 */
public class DigestCredentials implements Credentials{
    public static final String SCHEME = "Digest";

    public static final String USERNAME = "username";
    public static final String REALM = "realm";
    public static final String NONCE = "nonce";
    public static final String URI = "uri";
    public static final String RESPONSE = "response";
    public static final String ALGORITHM = "algorithm";
    public static final String CNONCE = "cnonce";
    public static final String OPAQUE = "opaque";
    public static final String QOP = "qop";
    public static final String NC = "nc";

    // mandatory
    public String username;
    public String realm;
    public String nonce;
    public String uri;
    public String response;

    // optional
    public String algorithm;
    public String cnonce;
    public String opaque;
    public String qop;
    public String nc;

    public DigestCredentials(){}

    public DigestCredentials(String value){
        Parser parser = new Parser(true, value);
        while(true){
            String name = parser.lvalue();
            if(name==null)
                break;
            if(USERNAME.equalsIgnoreCase(name))
                username = parser.rvalue();
            else if(REALM.equalsIgnoreCase(name))
                realm = parser.rvalue();
            else if(NONCE.equalsIgnoreCase(name))
                nonce = parser.rvalue();
            else if(URI.equalsIgnoreCase(name))
                uri = parser.rvalue();
            else if(RESPONSE.equalsIgnoreCase(name))
                response = parser.rvalue();
            else if(ALGORITHM.equalsIgnoreCase(name))
                algorithm = parser.rvalue();
            else if(CNONCE.equalsIgnoreCase(name))
                cnonce = parser.rvalue();
            else if(OPAQUE.equalsIgnoreCase(name))
                opaque = parser.rvalue();
            else if(QOP.equalsIgnoreCase(name))
                qop = parser.rvalue();
            else if(NC.equalsIgnoreCase(name))
                nc = parser.rvalue();
            parser.skipPairs();
            parser.skip();
        }

        requireNonNull(username, "username==null");
        requireNonNull(realm, "realm==null");
        requireNonNull(nonce, "nonce==null");
        requireNonNull(uri, "uri==null");
        requireNonNull(response, "response==null");
    }

    @Override
    public String scheme(){
        return SCHEME;
    }

    @Override
    public String toString(){
        StringBuilder buffer = new StringBuilder();
        buffer.append(SCHEME).append(' ');

        Parser.appendQuotedValue(buffer, USERNAME, username);

        buffer.append(',');
        Parser.appendQuotedValue(buffer, REALM, realm);

        buffer.append(',');
        Parser.appendQuotedValue(buffer, NONCE, nonce);

        buffer.append(',');
        Parser.appendQuotedValue(buffer, URI, uri);

        buffer.append(',');
        Parser.appendQuotedValue(buffer, RESPONSE, response);

        if(algorithm!=null){
            buffer.append(',');
            buffer.append(ALGORITHM).append('=').append(algorithm);
        }
        if(cnonce!=null){
            buffer.append(',');
            Parser.appendQuotedValue(buffer, CNONCE, cnonce);
        }
        if(opaque!=null){
            buffer.append(',');
            Parser.appendQuotedValue(buffer, OPAQUE, opaque);
        }
        if(qop!=null){
            buffer.append(',');
            buffer.append(QOP).append('=').append(qop);
        }
        if(nc!=null){
            buffer.append(',');
            Parser.appendValue(buffer, NC, nc);
        }
        return buffer.toString();
    }
}
