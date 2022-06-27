package com.reandroid.arsctoproto.converters;

import com.reandroid.lib.arsc.value.BaseResValue;
import com.reandroid.lib.arsc.value.ResValueBag;
import com.reandroid.lib.arsc.value.ResValueInt;

public class ProtoValue extends ProtoBase<ProtoEntry, BaseResValue>{
    private com.android.aapt.Value.Builder mBuilder;
    public ProtoValue(){
        super();
    }
    @Override
    public void convertARSC(ProtoEntry ignored, BaseResValue block) {
        mBuilder=loadValueBuilder(block);
    }
    com.android.aapt.Value.Builder getValueBuilder(){
        return mBuilder;
    }
    private com.android.aapt.Value.Builder loadValueBuilder(BaseResValue block) {
        if(block instanceof ResValueInt){
            ResValueInt resValueInt=(ResValueInt)block;
            return loadInt(resValueInt);
        }
        if(block instanceof ResValueBag){
            ResValueBag resValueBag=(ResValueBag)block;
            return loadBag(resValueBag);
        }
        return null;
    }
    private com.android.aapt.Value.Builder loadBag(ResValueBag resValueBag) {
        ProtoItemBag protoItemBag=new ProtoItemBag();
        protoItemBag.setResourceFileChecker(getResourceFileChecker());
        protoItemBag.convertARSC(this, resValueBag);
        com.android.aapt.CompoundValue.Builder compoundBuilder = protoItemBag.getCompoundBuilder();
        if(compoundBuilder==null){
            return null;
        }
        com.android.aapt.Value.Builder builder= com.android.aapt.Value.newBuilder();
        builder.setCompoundValue(compoundBuilder);

        return builder;
    }
    private com.android.aapt.Value.Builder loadInt(ResValueInt resValueInt) {
        ProtoItemInt protoItemInt=new ProtoItemInt();
        protoItemInt.setResourceFileChecker(getResourceFileChecker());

        protoItemInt.convertARSC(this, resValueInt);
        com.android.aapt.Item.Builder itemBuilder = protoItemInt.getItemBuilder();
        if(itemBuilder==null){
            return null;
        }
        com.android.aapt.Value.Builder builder= com.android.aapt.Value.newBuilder();
        builder.setItem(itemBuilder);
        if(isId(resValueInt)){
            builder.setWeak(true);
        }
        return builder;
    }
}
