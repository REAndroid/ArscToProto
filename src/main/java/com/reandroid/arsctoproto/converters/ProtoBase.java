package com.reandroid.arsctoproto.converters;

import com.android.aapt.FileReference;
import com.android.aapt.Item;
import com.android.aapt.PBString;
import com.reandroid.lib.arsc.base.Block;
import com.reandroid.lib.arsc.value.ResValueBag;
import com.reandroid.lib.arsc.value.ResValueInt;
import com.reandroid.lib.arsc.value.attribute.AttributeBag;

public abstract class ProtoBase<PARENT extends ProtoBase, ARSC extends Block> {
    private ResourceFileChecker mResourceFileChecker;
    // parent is for ProtoEntry, ProtoPackage
    public abstract void convertARSC(PARENT parent, ARSC block);
    static void log(String text){
        System.out.println(text);
    }


    public Item.Builder convertStringValue(String value){
        Item.Builder itemBuilder= Item.newBuilder();
        if(isFile(value)){
            FileReference.Builder fileReferenceBuilder= FileReference.newBuilder();
            fileReferenceBuilder.setPath(value);
            FileReference.Type type=fromPath(value);
            if(type!=null){
                fileReferenceBuilder.setType(type);
            }
            itemBuilder.setFile(fileReferenceBuilder);
        }else {
            PBString.Builder strBuilder= PBString.newBuilder();
            strBuilder.setValue(value);
            itemBuilder.setStr(strBuilder);
        }
        return itemBuilder;
    }
    private FileReference.Type fromPath(String path){
        int i=path.lastIndexOf('.');
        if(i<1){
            return null;
        }
        String ext=path.substring(i);
        if(ext.equals(".xml")){
            return FileReference.Type.PROTO_XML;
        }
        if(ext.equals(".png")){
            return FileReference.Type.PNG;
        }
        return null;
    }
    public boolean isFile(String path){
        ResourceFileChecker fileChecker=getResourceFileChecker();
        if(fileChecker==null){
            return false;
        }
        return fileChecker.isResourceFile(path);
    }
    public ResourceFileChecker getResourceFileChecker() {
        return mResourceFileChecker;
    }
    public void setResourceFileChecker(ResourceFileChecker resourceFileChecker) {
        this.mResourceFileChecker = resourceFileChecker;
    }

    static boolean isId(ResValueInt resValueInt){
        String typeName=resValueInt.getEntryBlock().getTypeBlock().getTypeString().get();
        if("id".equals(typeName)){
            return true;
        }
        return false;
    }
    static boolean isStyle(ResValueBag resValueBag){
        String typeName=resValueBag.getEntryBlock().getTypeBlock().getTypeString().get();
        if("style".equals(typeName)){
            return true;
        }
        return false;
    }
    static boolean isArray(ResValueBag resValueBag){
        String typeName=resValueBag.getEntryBlock().getTypeBlock().getTypeString().get();
        if("array".equals(typeName)){
            return true;
        }
        return false;
    }
    static boolean isPlural(ResValueBag resValueBag){
        String typeName=resValueBag.getEntryBlock().getTypeBlock().getTypeString().get();
        if("plurals".equals(typeName)){
            return true;
        }
        return false;
    }
    static boolean isAttr(ResValueBag resValueBag){
        return AttributeBag.isAttribute(resValueBag);
    }
}
