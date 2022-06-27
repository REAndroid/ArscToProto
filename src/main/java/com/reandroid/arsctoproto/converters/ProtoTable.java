package com.reandroid.arsctoproto.converters;

import com.android.aapt.PBPackage;
import com.android.aapt.PackageId;
import com.android.aapt.ResourceTable;
import com.reandroid.lib.arsc.chunk.ChunkType;
import com.reandroid.lib.arsc.chunk.PackageBlock;
import com.reandroid.lib.arsc.chunk.TableBlock;
import com.reandroid.lib.arsc.header.HeaderBlock;
import com.reandroid.lib.arsc.io.BlockReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public class ProtoTable extends ProtoBase<ProtoTable, TableBlock>{
    private ResourceTable.Builder mBuilder;
    public ProtoTable(){
        super();
    }
    @Override
    public void convertARSC(ProtoTable protoTable, TableBlock block) {
        Collection<PackageBlock> allPkg = block.listPackages();
        for(PackageBlock packageBlock:allPkg){
            ProtoPackage protoPackage=new ProtoPackage();
            protoPackage.setResourceFileChecker(getResourceFileChecker());

            protoPackage.convertARSC(this, packageBlock);
        }
    }
    public PBPackage.Builder getOrCreate(int pkgId, String pkgName){
        ResourceTable.Builder tableBuilder=getTableBuilder();
        List<PBPackage.Builder> allPkg = tableBuilder.getPackageBuilderList();
        for(PBPackage.Builder pkg:allPkg){
            if(pkg.getPackageId().getId()==pkgId){
                return pkg;
            }
        }
        PBPackage.Builder packageBuilder = PBPackage.newBuilder();
        packageBuilder.setPackageName(pkgName);
        PackageId.Builder pkgIdBuilder=PackageId.newBuilder();
        pkgIdBuilder.setId(pkgId);
        packageBuilder.setPackageId(pkgIdBuilder);
        tableBuilder.addPackage(packageBuilder);

        allPkg = tableBuilder.getPackageBuilderList();
        for(PBPackage.Builder pkg:allPkg){
            if(pkg.getPackageId().getId()==pkgId){
                return pkg;
            }
        }
        return packageBuilder;
    }

    public ResourceTable.Builder getTableBuilder(){
        if(mBuilder==null){
            mBuilder=ResourceTable.newBuilder();
        }
        return mBuilder;
    }


    public static ResourceTable.Builder toResourceTable(File resourcesARSC, ResourceFileChecker resourceChecker) throws IOException {
        InputStream inputStream=new FileInputStream(resourcesARSC);
        return toResourceTable(inputStream, resourceChecker);
    }
    public static ResourceTable.Builder toResourceTable(InputStream inputStream, ResourceFileChecker resourceChecker) throws IOException {
        BlockReader reader=new BlockReader(inputStream);
        HeaderBlock header = reader.readHeaderBlock();
        if(header==null){
            throw new IOException("Null header block");
        }
        ChunkType chunkType = header.getChunkType();

        if(chunkType!=ChunkType.TABLE){
            throw new IOException("Invalid TABLE header chunk type: "+header.toString());
        }
        TableBlock tableBlock=new TableBlock();
        tableBlock.readBytes(reader);

        return toResourceTable(tableBlock, resourceChecker);
    }

    public static ResourceTable.Builder toResourceTable(TableBlock tableBlock, ResourceFileChecker resourceChecker){
        ProtoTable protoTable=new ProtoTable();
        protoTable.setResourceFileChecker(resourceChecker);
        protoTable.convertARSC(protoTable, tableBlock);
        ResourceTable.Builder builder = protoTable.getTableBuilder();
        return builder;
    }
}
