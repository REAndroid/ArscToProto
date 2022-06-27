package com.reandroid.arsctoproto.converters;

import com.android.aapt.PBPackage;
import com.android.aapt.PBType;
import com.reandroid.lib.arsc.chunk.PackageBlock;
import com.reandroid.lib.arsc.chunk.TypeBlock;
import com.reandroid.lib.arsc.group.EntryGroup;
import com.reandroid.lib.arsc.value.EntryBlock;

import java.util.Collection;
import java.util.List;

public class ProtoPackage extends ProtoBase<ProtoTable, PackageBlock> {
    private PBPackage.Builder mBuilder;
    public ProtoPackage(){
        super();
    }
    @Override
    public void convertARSC(ProtoTable parent, PackageBlock block) {
        int pkgId=block.getPackageId();
        String pkgName=block.getPackageName();
        mBuilder= parent.getOrCreate(pkgId, pkgName);

        Collection<EntryGroup> entryGroupList = block.listEntryGroup();

        loadEntryGroups(entryGroupList);
    }
    private void loadEntryGroups(Collection<EntryGroup> entryGroupList){
        for(EntryGroup entryGroup:entryGroupList){
            loadEntryGroup(entryGroup);
        }
    }
    private void loadEntryGroup(EntryGroup entryGroup){
        List<EntryBlock> entryBlockList = entryGroup.listItems();
        loadEntries(entryBlockList);
    }
    private void loadEntries(List<EntryBlock> entryBlockList){
        for(EntryBlock entryBlock:entryBlockList){
            loadEntry(entryBlock);
        }
    }
    private void loadEntry(EntryBlock entryBlock){
        ProtoEntry protoEntry=new ProtoEntry();
        protoEntry.setResourceFileChecker(getResourceFileChecker());
        protoEntry.convertARSC(this, entryBlock);
    }
    PBType.Builder getOrCreateType(TypeBlock typeBlock){
        int typeId=typeBlock.getTypeId();
        String typeName=typeBlock.getTypeString().get();
        return getOrCreateType(typeId, typeName);
    }
    PBType.Builder getOrCreateType(int typeId, String name){
        PBPackage.Builder pkgBuilder= getPackageBuilder();
        List<PBType.Builder> builderList = pkgBuilder.getTypeBuilderList();
        for(PBType.Builder type:builderList){
            if(typeId==type.getTypeId().getId()){
                return type;
            }
        }
        PBType.Builder typeBuilder= PBType.newBuilder();
        com.android.aapt.TypeId.Builder typeIdBuilder=com.android.aapt.TypeId.newBuilder();
        typeIdBuilder.setId(typeId);
        typeBuilder.setTypeId(typeIdBuilder);
        typeBuilder.setName(name);

        pkgBuilder.addType(typeBuilder);

        builderList = pkgBuilder.getTypeBuilderList();
        for(PBType.Builder type:builderList){
            if(typeId==type.getTypeId().getId()){
                return type;
            }
        }
        return typeBuilder;
    }
    public PBPackage.Builder getPackageBuilder(){
        if(mBuilder==null){
            mBuilder= PBPackage.newBuilder();
        }
        return mBuilder;
    }
}
