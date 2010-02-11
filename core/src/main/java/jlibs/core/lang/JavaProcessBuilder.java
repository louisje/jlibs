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

package jlibs.core.lang;

import jlibs.core.io.FileNavigator;
import jlibs.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to create a java process.
 *
 * by default, javaHome and workingDir are picked from
 * current process.
 *
 * any relative path specified will get resolved relative
 * to working directory lazily.
 *
 * This class favors method chaining.
 *
 * @author Santhosh Kumar T
 */
public class JavaProcessBuilder{
    /*-------------------------------------------------[ java-home ]---------------------------------------------------*/

    private File javaHome = FileUtil.JAVA_HOME;

    public JavaProcessBuilder javaHome(File javaHome){
        this.javaHome = javaHome;
        return this;
    }

    public File javaHome(){
        return javaHome;
    }

    /*-------------------------------------------------[ working-dir ]---------------------------------------------------*/

    private File workingDir = FileUtil.USER_DIR;

    public JavaProcessBuilder workingDir(String dir){
        return workingDir(new File(dir));
    }

    public JavaProcessBuilder workingDir(File dir){
        workingDir = dir;
        return this;
    }

    public File workingDir(){
        return workingDir;
    }

    /*-------------------------------------------------[ classpath ]---------------------------------------------------*/

    private List<File> classpath = new ArrayList<File>();

    public JavaProcessBuilder classpath(String resource){
        return classpath(new File(resource));
    }

    public JavaProcessBuilder classpath(File resource){
        classpath.add(resource);
        return this;
    }

    public List<File> classpath(){
        return classpath;
    }

    /*-------------------------------------------------[ endorsed-dirs ]---------------------------------------------------*/

    private List<File> endorsedDirs = new ArrayList<File>();

    public JavaProcessBuilder endorsedDir(String dir){
        return endorsedDir(new File(dir));
    }

    public JavaProcessBuilder endorsedDir(File dir){
        endorsedDirs.add(dir);
        return this;
    }

    public List<File> endorsedDirs(){
        return endorsedDirs;
    }

    /*-------------------------------------------------[ ext-dirs ]---------------------------------------------------*/

    private List<File> extDirs = new ArrayList<File>();

    public JavaProcessBuilder extDir(String dir){
        return extDir(new File(dir));
    }

    public JavaProcessBuilder extDir(File dir){
        extDirs.add(dir);
        return this;
    }

    public List<File> extDirs(){
        return extDirs;
    }

    /*-------------------------------------------------[ library-path ]---------------------------------------------------*/

    private List<File> libraryPath = new ArrayList<File>();

    public JavaProcessBuilder libraryPath(String dir){
        return libraryPath(new File(dir));
    }

    public JavaProcessBuilder libraryPath(File dir){
        libraryPath.add(dir);
        return this;
    }

    public List<File> libraryPath(){
        return libraryPath;
    }

    /*-------------------------------------------------[ boot-classpath ]---------------------------------------------------*/

    private List<File> bootClasspath = new ArrayList<File>();

    public JavaProcessBuilder bootClasspath(String resource){
        return bootClasspath(new File(resource));
    }

    public JavaProcessBuilder bootClasspath(File resource){
        bootClasspath.add(resource);
        return this;
    }

    public List<File> bootClasspath(){
        return bootClasspath;
    }

    /*-------------------------------------------------[ append-boot-classpath ]---------------------------------------------------*/

    private List<File> appendBootClasspath = new ArrayList<File>();

    public JavaProcessBuilder appendBootClasspath(String resource){
        return appendBootClasspath(new File(resource));
    }

    public JavaProcessBuilder appendBootClasspath(File resource){
        appendBootClasspath.add(resource);
        return this;
    }

    public List<File> appendBootClasspath(){
        return appendBootClasspath;
    }

    /*-------------------------------------------------[ prepend-boot-classpath ]---------------------------------------------------*/

    private List<File> prependBootClasspath = new ArrayList<File>();

    public JavaProcessBuilder prependBootClasspath(String resource){
        return prependBootClasspath(new File(resource));
    }

    public JavaProcessBuilder prependBootClasspath(File resource){
        prependBootClasspath.add(resource);
        return this;
    }

    public List<File> prependBootClasspath(){
        return prependBootClasspath;
    }

    /*-------------------------------------------------[ system-properties ]---------------------------------------------------*/

    private Map<String, String> systemProperties = new HashMap<String, String>();

    public JavaProcessBuilder systemProperty(String name, String value){
        systemProperties.put(name, value);
        return this;
    }

    public JavaProcessBuilder systemProperty(String name){
        return systemProperty(name, null);
    }

    public Map<String, String> systemProperties(){
        return systemProperties;
    }

    /*-------------------------------------------------[ initial-heap ]---------------------------------------------------*/

    private String initialHeap;

    public JavaProcessBuilder initialHeap(int mb){
        return initialHeap(mb+"m");
    }

    public JavaProcessBuilder initialHeap(String size){
        initialHeap = size;
        return this;
    }

    public String initialHeap(){
        return initialHeap;
    }

    /*-------------------------------------------------[ max-heap ]---------------------------------------------------*/

    private String maxHeap;

    public JavaProcessBuilder maxHeap(int mb){
        return maxHeap(mb+"m");
    }

    public JavaProcessBuilder maxHeap(String size){
        maxHeap = size;
        return this;
    }

    public String maxHeap(){
        return maxHeap;
    }

    /*-------------------------------------------------[ vm-type ]---------------------------------------------------*/

    private String vmType;

    public JavaProcessBuilder client(){
        vmType = "-client";
        return this;
    }

    public JavaProcessBuilder server(){
        vmType = "-server";
        return this;
    }

    public String vmType(){
        return vmType;
    }

    /*-------------------------------------------------[ jvm-args ]---------------------------------------------------*/

    private List<String> jvmArgs = new ArrayList<String>();

    public JavaProcessBuilder jvmArg(String arg){
        jvmArgs.add(arg);
        return this;
    }

    public List<String> jvmArgs(){
        return jvmArgs;
    }

    /*-------------------------------------------------[ main-class ]---------------------------------------------------*/

    private String mainClass;

    public JavaProcessBuilder mainClass(String mainClass){
        this.mainClass = mainClass;
        return this;
    }

    public String mainClass(){
        return mainClass;
    }

    /*-------------------------------------------------[ debug ]---------------------------------------------------*/

    private boolean debugSuspend;

    public JavaProcessBuilder debugSuspend(boolean suspend){
        this.debugSuspend = suspend;
        return this;
    }

    public boolean debugSuspend(){
        return debugSuspend;
    }

    private int debugPort = -1;

    public JavaProcessBuilder debugPort(int port){
        this.debugPort = port;
        return this;
    }

    public int debugPort(){
        return debugPort;
    }

    /*-------------------------------------------------[ arguments ]---------------------------------------------------*/

    private List<String> args = new ArrayList<String>();

    public JavaProcessBuilder arg(String arg){
        args.add(arg);
        return this;
    }

    public List<String> args(){
        return args;
    }

    /*-------------------------------------------------[ Command ]---------------------------------------------------*/

    private static String toString(File fromDir, Iterable<File> files) throws IOException{
        StringBuilder buff = new StringBuilder();
        for(File file: files){
            if(buff.length()>0)
                buff.append(FileUtil.PATH_SEPARATOR);
            if(fromDir==null)
                buff.append(file.getCanonicalPath());
            else
                buff.append(FileNavigator.INSTANCE.getRelativePath(fromDir, file));
        }
        return buff.toString();
    }

    /** Returns command with all its arguments */
    public String[] command() throws IOException{
        List<String> cmd = new ArrayList<String>();

        String executable = javaHome.getCanonicalPath()+FileUtil.SEPARATOR+"bin"+FileUtil.SEPARATOR+"java";
        if(OS.get().isWindows())
            executable += ".exe";
        cmd.add(executable);

        String path = toString(workingDir, prependBootClasspath);
        if(path.length()>0)
            cmd.add("-Xbootclasspath/p:"+path);

        path = toString(workingDir, bootClasspath);
        if(path.length()>0)
            cmd.add("-Xbootclasspath:"+path);

        path = toString(workingDir, appendBootClasspath);
        if(path.length()>0)
            cmd.add("-Xbootclasspath/a:"+path);

        path = toString(workingDir, classpath);
        if(path.length()>0){
            cmd.add("-classpath");
            cmd.add(path);
        }

        path = toString(workingDir, extDirs);
        if(path.length()>0)
            cmd.add("-Djava.ext.dirs="+path);

        path = toString(workingDir, endorsedDirs);
        if(path.length()>0)
            cmd.add("-Djava.endorsed.dirs="+path);

        path = toString(workingDir, libraryPath);
        if(path.length()>0)
            cmd.add("-Djava.library.path="+path);

        for(Map.Entry<String, String> prop: systemProperties.entrySet()){
            if(prop.getValue()==null)
                cmd.add("-D"+prop.getKey());
            else
                cmd.add("-D"+prop.getKey()+"="+prop.getValue());
        }

        if(initialHeap!=null)
            cmd.add("-Xms"+initialHeap);
        if(maxHeap!=null)
            cmd.add("-Xmx"+maxHeap);
        if(vmType!=null)
            cmd.add(vmType);
        if(debugPort!=-1){
            cmd.add("-Xdebug");
            cmd.add("-Xnoagent");
            cmd.add("-Xrunjdwp:transport=dt_socket,server=y,suspend="+(debugSuspend?'y':'n')+",address="+debugPort);
        }
        cmd.addAll(jvmArgs);
        if(mainClass!=null){
            cmd.add(mainClass);
            cmd.addAll(args);
        }

        return cmd.toArray(new String[cmd.size()]);
    }

    /**
     * launches jvm with current configuration.
     *
     * note that, the streams passed are not closed automatically.
     *
     * @param output    outputstream to which process's input stream to be redirected.
     *                  if null, it is not redirected
     * @param error     outputstream to which process's error stream to be redirected.
     *                  if null, it is not redirected
     * @return          the process created
     *
     * @see jlibs.core.lang.RuntimeUtil#redirectStreams(Process, java.io.OutputStream, java.io.OutputStream) 
     */
    public Process launch(OutputStream output, OutputStream error) throws IOException{
        Process process = Runtime.getRuntime().exec(command(), null, workingDir);
        RuntimeUtil.redirectStreams(process, output, error);
        return process;
    }
}
