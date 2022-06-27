package com.reandroid.arsctoproto.converters;

import com.android.aapt.PBType;
import com.reandroid.lib.arsc.value.EntryBlock;
import com.reandroid.lib.arsc.value.ResConfig;


public class ProtoEntry extends ProtoBase<ProtoPackage, EntryBlock>{
    private com.android.aapt.Entry.Builder mBuilder;
    public ProtoEntry(){
        super();
    }
    @Override
    public void convertARSC(ProtoPackage parent, EntryBlock block) {
        PBType.Builder typeBuilder = parent.getOrCreateType(block.getTypeBlock());
        ResConfig resConfig = block.getResConfig();
        String entryName=block.getSpecString().get();


        com.android.aapt.ConfigValue.Builder configBuilder=ProtoHelperConfig.convertConfigValue(resConfig);


        com.android.aapt.Entry.Builder entryBuilder=getEntryBuilder();
        entryBuilder.setName(entryName);

        com.android.aapt.EntryId.Builder entryIdBuilder=com.android.aapt.EntryId.newBuilder();
        entryIdBuilder.setId(block.getIndex());

        entryBuilder.setEntryId(entryIdBuilder);

        ProtoValue protoValue=new ProtoValue();
        protoValue.setResourceFileChecker(getResourceFileChecker());
        protoValue.convertARSC(this, block.getResValue());

        com.android.aapt.Value.Builder valueBuilder= protoValue.getValueBuilder();
        if(valueBuilder==null){
            return;
        }
        configBuilder.setValue(valueBuilder);

        entryBuilder.addConfigValue(configBuilder);

        typeBuilder.addEntry(entryBuilder.build());

    }
    private com.android.aapt.Entry.Builder getEntryBuilder(){
        if(mBuilder==null){
            mBuilder=com.android.aapt.Entry.newBuilder();
        }
        return mBuilder;
    }
}
