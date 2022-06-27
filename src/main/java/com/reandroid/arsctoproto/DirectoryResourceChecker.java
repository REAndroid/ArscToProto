package com.reandroid.arsctoproto;

import com.reandroid.arsctoproto.converters.ResourceFileChecker;
import com.reandroid.lib.arsc.chunk.xml.ResXmlBlock;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class DirectoryResourceChecker implements ResourceFileChecker {
    private final File mDir;
    private final Set<File> mResXmlFiles;
    private final Set<File> mResOtherFiles;
    public DirectoryResourceChecker(File dir){
        this.mDir=dir;
        mResXmlFiles=new HashSet<>();
        mResOtherFiles=new HashSet<>();
    }
    @Override
    public boolean isResourceFile(String path) {
        if(path==null){
            return false;
        }
        File file=toFile(path);
        if(file.isFile()){
            addResFile(file);
            return true;
        }
        return false;
    }

    public boolean contains(File file){
        if(mResXmlFiles.contains(file)){
            return true;
        }
        return mResOtherFiles.contains(file);
    }
    public Set<File> getResXmlFiles() {
        return mResXmlFiles;
    }
    public Set<File> getResOtherFiles() {
        return mResOtherFiles;
    }

    private void addResFile(File file){
        if(contains(file)){
            return;
        }
        if(!ResXmlBlock.isResXmlBlock(file)){
            mResOtherFiles.add(file);
            return;
        }
        mResXmlFiles.add(file);
    }
    private File toFile(String path){
        path=path.replace('/', File.separatorChar);
        File file=new File(mDir, path);
        return file;
    }
}
